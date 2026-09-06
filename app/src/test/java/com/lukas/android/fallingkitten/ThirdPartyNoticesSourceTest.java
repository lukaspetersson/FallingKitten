package com.lukas.android.fallingkitten;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

public class ThirdPartyNoticesSourceTest {
    @Test
    public void packagedAssetIsCanonicalCandidateSourceWithKnownOpenGates() throws Exception {
        Path asset = Paths.get("src/main/assets/third_party_notices.md");
        String notices = new String(Files.readAllBytes(asset), StandardCharsets.UTF_8);
        assertFalse(notices.isBlank());
        assertTrue(notices.contains("GOOGLE MATERIAL ICONS ROUND — VOLUME ICON DERIVATIVES"));
        assertTrue(notices.contains("84ccef280841abfac506afc4ad4a2782f6d0a1d0"));
        assertTrue(notices.contains("src/av/volume_up/materialiconsround/24px.svg"));
        assertTrue(notices.contains("src/av/volume_off/materialiconsround/24px.svg"));
        assertTrue(notices.contains("AndroidX AppCompat 1.7.1"));
        assertTrue(notices.contains("ConstraintLayout 2.2.1"));
        assertTrue(notices.contains("their packaged AndroidX transitive components"));
        assertTrue(notices.contains("ANDROID GIF DRAWABLE 1.2.29 — INCLUDING NATIVE PAYLOAD"));
        assertTrue(notices.contains("libpl_droidsonroids_gif.so for arm64-v8a, armeabi-v7a, x86, and x86_64"));
        assertTrue(notices.contains("MONTSERRAT FONT"));
        assertTrue(notices.contains("555facfb2a18c72c3c0380f0d9c0f060453a9058"));
        assertTrue(notices.contains("FIRST-PARTY SYNTHETIC AUDIO"));
        assertTrue(notices.contains("meow2.mp3 and meow3.mp3 files are first-party synthetic audio"));
        assertFalse(notices.contains("SoundBible.com"));
        assertFalse(notices.contains("Mike Koenig"));
    }

    @Test
    public void repositoryNoticeIsOnlyAPointerToPackagedCanonicalSource() throws Exception {
        String pointer = new String(Files.readAllBytes(Paths.get("../THIRD_PARTY_NOTICES.md")), StandardCharsets.UTF_8);
        assertTrue(pointer.contains("`app/src/main/assets/third_party_notices.md`"));
        assertTrue(pointer.contains("Do not maintain a second notice body here."));
        assertFalse(pointer.contains("Apache License\n"));
    }
}
