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
credits_layout=Path('app/src/main/res/layout/activity_credits.xml').read_text()
notices_layout=Path('app/src/main/res/layout/activity_third_party_notices.xml').read_text()
main_activity=Path('app/src/main/java/com/lukas/android/fallingkitten/MainActivity.java').read_text()
credits_activity=Path('app/src/main/java/com/lukas/android/fallingkitten/Credits.java').read_text()
notices_activity=Path('app/src/main/java/com/lukas/android/fallingkitten/ThirdPartyNoticesActivity.java').read_text()
notices=Path('app/src/main/assets/third_party_notices.md').read_text(encoding='utf-8')
notice_pointer=Path('THIRD_PARTY_NOTICES.md').read_text(encoding='utf-8')
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
assert 'android:onClick="Credits"' in Path('app/src/main/res/layout/activity_main.xml').read_text()
assert re.search(r'void\s+Credits\s*\(View view\)', main_activity)
assert 'android:onClick="openThirdPartyNotices"' in credits_layout
assert 'void openThirdPartyNotices(View view)' in credits_activity
assert 'ThirdPartyNoticesActivity.class' in credits_activity
assert 'android:name=".Credits"' in manifest and 'android:name=".ThirdPartyNoticesActivity"' in manifest
assert manifest.count('android:exported="false"') >= 2
assert 'getAssets().open("third_party_notices.md")' in notices_activity
assert 'StandardCharsets.UTF_8' in notices_activity
assert 'ScrollView' in notices_layout and 'closeNotices' in notices_layout
assert 'CANDIDATE STATUS' in notices
for required in ('ANDROIDX APPCOMPAT 1.7.1', 'ANDROIDX CONSTRAINTLAYOUT 2.2.1', 'ANDROID GIF DRAWABLE 1.2.29', 'MONTSERRAT FONT', 'AUDIO CREDIT — CAT MEOW', 'AUDIO CREDIT — KITTEN MEOW'):
    assert required in notices, required
for unresolved in ('transitive/native', 'volume-icon', 'Axel-created', 'exact-AAB', 'legal sufficiency'):
    assert unresolved in notices, unresolved
assert '`app/src/main/assets/third_party_notices.md`' in notice_pointer
assert 'Do not maintain a second notice body here.' in notice_pointer
print('Static checks passed: XML, identity/version, SDK/JDK/toolchain, AndroidX, exports/backup, offline high score, Credits notice route, canonical packaged notice, and explicit open gates.')
PY
