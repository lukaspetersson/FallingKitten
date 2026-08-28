package com.lukas.android.fallingkitten;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.Test;

public class VolumeIconReplacementTest {
    private static final String UP = "M3,10v4c0,0.55 0.45,1 1,1h3l3.29,3.29c0.63,0.63 1.71,0.18 1.71,-0.71V6.41c0,-0.89 -1.08,-1.34 -1.71,-0.71L7,9H4c-0.55,0 -1,0.45 -1,1zm13.5,2c0,-1.77 -1.02,-3.29 -2.5,-4.03v8.05c1.48,-0.73 2.5,-2.25 2.5,-4.02zM14,4.45v0.2c0,0.38 0.25,0.71 0.6,0.85C17.18,6.53 19,9.06 19,12s-1.82,5.47 -4.4,6.5c-0.36,0.14 -0.6,0.47 -0.6,0.85v0.2c0,0.63 0.63,1.07 1.21,0.85C18.6,19.11 21,15.84 21,12s-2.4,-7.11 -5.79,-8.4c-0.58,-0.23 -1.21,0.22 -1.21,0.85z";
    private static final String OFF = "M3.63,3.63c-0.39,0.39 -0.39,1.02 0,1.41L7.29,8.7 7,9H4c-0.55,0 -1,0.45 -1,1v4c0,0.55 0.45,1 1,1h3l3.29,3.29c0.63,0.63 1.71,0.18 1.71,-0.71v-4.17l4.18,4.18c-0.49,0.37 -1.02,0.68 -1.6,0.91 -0.36,0.15 -0.58,0.53 -0.58,0.92 0,0.72 0.73,1.18 1.39,0.91 0.8,-0.33 1.55,-0.77 2.22,-1.31l1.34,1.34c0.39,0.39 1.02,0.39 1.41,0 0.39,-0.39 0.39,-1.02 0,-1.41L5.05,3.63c-0.39,-0.39 -1.02,-0.39 -1.42,0zM19,12c0,0.82 -0.15,1.61 -0.41,2.34l1.53,1.53c0.56,-1.17 0.88,-2.48 0.88,-3.87 0,-3.83 -2.4,-7.11 -5.78,-8.4 -0.59,-0.23 -1.22,0.23 -1.22,0.86v0.19c0,0.38 0.25,0.71 0.61,0.85C17.18,6.54 19,9.06 19,12zm-8.71,-6.29l-0.17,0.17L12,7.76V6.41c0,-0.89 -1.08,-1.33 -1.71,-0.7zM16.5,12c0,-1.77 -1.02,-3.29 -2.5,-4.03v1.79l2.48,2.48c0.01,-0.08 0.02,-0.16 0.02,-0.24z";
    private static final Set<String> OLD_HASHES = new HashSet<>(Arrays.asList(
        "2fc6a4236a3f6b4c58ef2d7dbd99b0859d7fa83d41b2deef1f8c3f15a0c103c9", "47efbed0a283e167609f54dd5d4f3899826eb3801e2368ed15ffdc36bd91eb0f", "6357c84f63653e2291539e5e25e328025227c7b620bead8eae814a4d6c1af05d", "6d8582ff02d47de2d4a22f56ff60cd766746a44fd9211afda3119b9a17571996", "82bb23ce3da3d877dbd8f165731448b39232f48a31cf30b75fc0c8cda7acd73e", "9499fbe9467c80c439c498f0a043482b2e15b6763f256c6d2380665cd244cee6", "ad826c8db7d322e8161da82aa11d0f68ed99424d688d59d413143e3230dda71e", "defac2a0c21887f8975adf8adf10dbda362cc30138da8fc89488e0173bdb8950", "ee8cf74579378ae1706bbec911c32abfab7e6f39de527e0dc9c402fda769da9e", "f3956624d566221905332eb6e642904de7aea7efca36d26684a8b8de39519cf6"));

    @Test public void onlyUnqualifiedVectorsRemainAndOldRasterContentIsAbsent() throws Exception {
        Path res = Paths.get("src/main/res");
        try (Stream<Path> paths = Files.walk(res)) {
            for (Path path : (Iterable<Path>) paths.filter(Files::isRegularFile)::iterator) {
                String name = path.getFileName().toString();
                assertFalse("old PNG path remains: " + path, name.matches("round_volume_(up|off)_white_48\\.png"));
                assertFalse("old raster hash remains: " + path, OLD_HASHES.contains(hex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(path)))));
            }
        }
        assertTrue(Files.isRegularFile(res.resolve("drawable/round_volume_up_white_48.xml")));
        assertTrue(Files.isRegularFile(res.resolve("drawable/round_volume_off_white_48.xml")));
    }

    @Test public void vectorsHaveReviewedDimensionsWhiteFillAndExactPathData() throws Exception {
        assertVector("round_volume_up_white_48.xml", UP);
        assertVector("round_volume_off_white_48.xml", OFF);
    }

    @Test public void noticeProvenanceAlignsWithBothAssetsAndReferencesStayUnchanged() throws Exception {
        String notice = read("src/main/assets/third_party_notices.md");
        for (String value : Arrays.asList("84ccef280841abfac506afc4ad4a2782f6d0a1d0", "src/av/volume_up/materialiconsround/24px.svg", "ed3b9e05cde06edf7ee7e5fc51bb9ba217032402", "0f6524fec9a2ddd04de1094082656779754df3e906691d2f9e4346835048d8f7", "src/av/volume_off/materialiconsround/24px.svg", "b4781cc2e0a627aecdcc0d7569920f31fbd82bf3", "02df0aca3f04993a1d80bc09aae704b035a093915f625cbc36b98241ecbbdaf1", "Apache License, Version 2.0", "SVG to Android VectorDrawable", "opaque white fill", "48dp dimensions", "Android parser safety", "explicit-zero form", "without changing numeric values, commands, order, fill, dimensions, viewport, resource basenames, or references", UP, OFF)) assertTrue(value, notice.contains(value));
        String layout = read("src/main/res/layout/activity_play.xml");
        String play = read("src/main/java/com/lukas/android/fallingkitten/Play.java");
        assertEquals(1, occurrences(layout, "@drawable/round_volume_up_white_48"));
        assertEquals(1, occurrences(play, "R.drawable.round_volume_off_white_48"));
        assertEquals(1, occurrences(play, "R.drawable.round_volume_up_white_48"));
    }

    private static void assertVector(String name, String pathData) throws Exception {
        String xml = read("src/main/res/drawable/" + name);
        assertTrue(xml.contains("android:width=\"48dp\"")); assertTrue(xml.contains("android:height=\"48dp\""));
        assertTrue(xml.contains("android:viewportWidth=\"24\"")); assertTrue(xml.contains("android:viewportHeight=\"24\""));
        assertTrue(xml.contains("android:fillColor=\"#FFFFFFFF\"")); assertTrue(xml.contains("android:pathData=\"" + pathData + "\""));
    }
    private static String read(String path) throws Exception { return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8); }
    private static int occurrences(String text, String needle) { int n=0,p=0; while ((p=text.indexOf(needle,p))>=0) { n++; p+=needle.length(); } return n; }
    private static String hex(byte[] bytes) { StringBuilder s=new StringBuilder(); for(byte b:bytes)s.append(String.format("%02x",b)); return s.toString(); }
}
