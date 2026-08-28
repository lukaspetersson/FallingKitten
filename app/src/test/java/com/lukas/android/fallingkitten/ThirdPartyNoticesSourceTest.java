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
        assertTrue(notices.contains("CANDIDATE STATUS"));
        assertTrue(notices.contains("complete resolved transitive/native dependency set"));
        assertTrue(notices.contains("GOOGLE MATERIAL ICONS ROUND — VOLUME ICONS"));
        assertTrue(notices.contains("runtime/visual cross-density and accessibility validation"));
        assertTrue(notices.contains("84ccef280841abfac506afc4ad4a2782f6d0a1d0"));
        assertTrue(notices.contains("58d1e17ffe5109a7ae296caafcadfdbe6a7d176f0bc4ab01e12a689b0499d8bd"));
        assertTrue(notices.contains("Axel-created code and artwork"));
        assertTrue(notices.contains("signed-AAB contents and automated reachability"));
        assertTrue(notices.contains("legal sufficiency"));
        assertTrue(notices.contains("ANDROIDX APPCOMPAT 1.7.1"));
        assertTrue(notices.contains("ANDROIDX CONSTRAINTLAYOUT 2.2.1"));
        assertTrue(notices.contains("ANDROID GIF DRAWABLE 1.2.29"));
        assertTrue(notices.contains("MONTSERRAT FONT"));
        assertTrue(notices.contains("AUDIO CREDIT — CAT MEOW"));
        assertTrue(notices.contains("AUDIO CREDIT — KITTEN MEOW"));
    }

    @Test
    public void repositoryNoticeIsOnlyAPointerToPackagedCanonicalSource() throws Exception {
        String pointer = new String(Files.readAllBytes(Paths.get("../THIRD_PARTY_NOTICES.md")), StandardCharsets.UTF_8);
        assertTrue(pointer.contains("`app/src/main/assets/third_party_notices.md`"));
        assertTrue(pointer.contains("Do not maintain a second notice body here."));
        assertFalse(pointer.contains("Apache License\n"));
    }
}
