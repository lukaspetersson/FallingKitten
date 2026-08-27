#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
python3 - <<'PY'
from pathlib import Path
import re, xml.etree.ElementTree as ET
for p in Path('app/src').rglob('*.xml'):
    ET.parse(p)
gradle=Path('app/build.gradle').read_text()
manifest=Path('app/src/main/AndroidManifest.xml').read_text()
strings=Path('app/src/main/res/values/strings.xml').read_text()
layout=Path('app/src/main/res/layout/activity_play.xml').read_text()
play=Path('app/src/main/java/com/lukas/android/fallingkitten/Play.java').read_text()
assert "namespace 'com.lukas.android.fallingkitten'" in gradle
assert "applicationId 'com.lukas.android.fallingkitten'" in gradle
assert re.search(r'compileSdk\s+36\b', gradle)
assert re.search(r'targetSdk\s+36\b', gradle)
assert re.search(r'versionCode\s+3\b', gradle)
assert re.search(r"versionName\s+'1\.2'", gradle)
assert 'JavaVersion.VERSION_17' in gradle
assert 'package="' not in manifest
assert 'android:exported="true"' in manifest
assert 'android:allowBackup="false"' in manifest
assert 'android:dataExtractionRules="@xml/data_extraction_rules"' in manifest
assert 'high_score' in strings and 'new_high_score' in strings
assert '@string/high_score_initial' in layout
for obsolete in ('com.google.android.gms', 'GoogleSignIn', 'Games.getLeaderboardsClient', 'leaderboard_id'):
    assert obsolete not in play and obsolete not in gradle and obsolete not in manifest and obsolete not in strings
for p in Path('app/src').rglob('*'):
    if p.is_file() and p.suffix in {'.java', '.xml'}:
        assert 'android.support.' not in p.read_text(), p
root_gradle=Path('build.gradle').read_text()
wrapper=Path('gradle/wrapper/gradle-wrapper.properties').read_text()
assert "version '8.10.1'" in root_gradle
assert 'gradle-8.11.1-bin.zip' in wrapper
print('Static checks passed: XML, identity/version, SDK/JDK/toolchain, AndroidX, exports/backup, offline high score.')
PY
