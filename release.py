#!/usr/bin/env python3

from shell import *
import re
import sys
import argparse
import getpass

defaultTargetDir = f'{HOME}/Documents/homepage/external/eclipse/java-simple'
defaultCertName = 'stefan wehr'
defaultSyncCmd = f'cd {HOME}/Documents/homepage && make sync'

def parseArgs():
    parser = argparse.ArgumentParser(description='Script for creating a release')
    parser.add_argument('--target-dir', metavar='DIR', type=str,
                        help=f'Target directory, default: {defaultTargetDir} (ATTENTION: will be erased)',
                        default=defaultTargetDir)
    parser.add_argument('--version', metavar='V', type=str, help='Version number for the release')
    parser.add_argument('--cert', metavar='NAME', type=str,
                        help=f'Name of certificate for signing jars, default: {defaultCertName}',
                        default=defaultCertName)
    parser.add_argument('--sync', metavar='CMD', type=str,
                        help=f'Command for syncing to webspace, default: {defaultSyncCmd}',
                        default=defaultSyncCmd)
    return parser.parse_args()

args = parseArgs()
targetDir = args.target_dir
certName = args.cert
version = args.version
syncCmd = args.sync
if version is None:
    abort('Missing --version argument')

versionRe = re.compile(r"^[.0-9]+$")
if not versionRe.match(version):
    abort(f'Invalid version: {version}')

versionFiles = ["hso.updateSite/site.xml", "hso.features.jsimple/feature.xml",
                "hso.jsimple/META-INF/MANIFEST.MF",
                "hso.plugins.jsimple/META-INF/MANIFEST.MF"]

re1 = re.compile(r"features/hso.features.jsimple_(?P<V>[.0-9]+).jar")
re2 = re.compile(r"""<feature[^>]*version\s*=\s*["'](?P<V>[.0-9]+)["']""")
re3 = re.compile(r"Bundle-Version:\s*(?P<V>[.0-9]+)")
res = [re1, re2, re3]

oldVersion = run("""grep '<feature.*version=' /Users/swehr/Documents/homepage/external/eclipse/java-simple/site.xml | sed 's/.*version="//g' | sed 's/".*//g'""", captureStdout=True).stdout.strip()
print(f'Old version: {oldVersion}')
print(f'New version: {version}')
input('Hit ENTER to continue ')

for f in versionFiles:
    content = open(f).read()
    idxs = [] # list of pairs (start, end)
    for r in res:
        for m in r.finditer(content):
            idxs.append( (m.start('V'), m.end('V')) )
    idxs.sort(reverse=True)
    for (start, end) in idxs:
        content = version.join([content[:start], content[end:]])
    open (f, 'w').write(content)
    print(f'Wrote new version {version} to {f}')

print('Now restart eclipse, open the project in eclipse, goto hso.updateSite/site.xml,')
print('and hit "Build All" (under "Site Map")')
input('Hit ENTER when build has finished')

def signJar(f, secret):
    print(f'Signing {f} ...')
    run(f'jarsigner -provider apple.security.AppleProvider -storetype KeychainStore -keystore NONE {f} "{certName}"',
        input=secret)

outputFiles = [f'plugins/hso.plugins.jsimple_{version}.jar',
               f'plugins/hso.jsimple_{version}.jar',
               'content.jar',
               f'features/hso.features.jsimple_{version}.jar',
               'artifacts.jar',
               'site.xml']

if isDir(targetDir):
    rmdir(targetDir, recursive=True)

secret = getpass.getpass('Password for keychain: ')

with workingDir('hso.updateSite'):
    for f in outputFiles:
        if getExt(f) == '.jar':
            signJar(f, secret)
        d = dirname(f)
        mkdir(pjoin(targetDir, d), createParents=True)
        cp(f, pjoin(targetDir, f))
        print(f'Copied {f} to {targetDir}')
        print()

print(f"""Finished! You should now do the following:
* Commit your changes.
* Create a git tag
* Sync the target directory {targetDir} to some webspace""")

input(f'Should I run the sync command "{syncCmd}"? Hit ENTER, Ctrl-C to abort')
run(syncCmd)
