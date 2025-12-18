import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import com.google.gson.*;

public final class InstallerMain {

    public static void main(String[] args) throws Exception {
        String packName = "Jugan Pack";
        String packId = "juganpack";
        String mcVersion = "1.21.1";
        String loaderVersion = "0.15.11";

        Path workDir = Paths.get("").toAbsolutePath();
        Path fabricInstaller = workDir.resolve("fabric-installer.jar");
        Path modsSrc = workDir.resolve("mods");
        Path configSrc = workDir.resolve("config");
        Path rpSrc = workDir.resolve("resourcepacks");
        Path optionsSrc = workDir.resolve("options.txt");

        requireFile(fabricInstaller, "Missing ./fabric-installer.jar next to the installer jar.");
        requireDir(modsSrc, "Missing ./mods folder next to the installer jar.");

        Path mcDir = detectMinecraftDir();
        requireDir(mcDir, "Minecraft folder not found. Launch Minecraft once, then rerun.");

        runFabricInstallerNoProfile(fabricInstaller, mcDir, mcVersion, loaderVersion);

        Path gameDir = pickGameDir(mcDir.resolve(packId));
        Files.createDirectories(gameDir);

        replaceDir(modsSrc, gameDir.resolve("mods"));
        if (Files.isDirectory(configSrc))
            replaceDir(configSrc, gameDir.resolve("config"));
        if (Files.isDirectory(rpSrc))
            replaceDir(rpSrc, gameDir.resolve("resourcepacks"));
        if (Files.isRegularFile(optionsSrc))
            copyFile(optionsSrc, gameDir.resolve("options.txt"));

        String lastVersionId = "fabric-loader-" + loaderVersion + "-" + mcVersion;
        addLauncherProfile(mcDir, packName, lastVersionId, gameDir);

        System.out.println("Done.");
        System.out.println("Open the Minecraft Launcher and select profile: " + packName);
    }

    private static void runFabricInstallerNoProfile(Path installerJar, Path mcDir, String mcVersion,
            String loaderVersion) throws Exception {
        List<String> cmd = List.of(
                javaBin(),
                "-jar", installerJar.toString(),
                "client",
                "-dir", mcDir.toString(),
                "-mcversion", mcVersion,
                "-loader", loaderVersion,
                "-noprofile");

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null)
                System.out.println(line);
        }

        int code = p.waitFor();
        if (code != 0)
            fatal("Fabric installer failed with exit code " + code);
    }

    private static void addLauncherProfile(Path mcDir, String packName, String lastVersionId, Path gameDir)
            throws IOException {
        Path profilesPath = mcDir.resolve("launcher_profiles.json");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject root;

        if (Files.isRegularFile(profilesPath)) {
            String json = Files.readString(profilesPath, StandardCharsets.UTF_8);
            root = JsonParser.parseString(json).getAsJsonObject();
        } else {
            root = new JsonObject();
        }

        JsonObject profiles = root.has("profiles") && root.get("profiles").isJsonObject()
                ? root.getAsJsonObject("profiles")
                : new JsonObject();
        root.add("profiles", profiles);

        for (Map.Entry<String, JsonElement> e : profiles.entrySet()) {
            if (e.getValue().isJsonObject()) {
                JsonObject p = e.getValue().getAsJsonObject();
                if (packName.equals(optString(p, "name"))) {
                    fatal("A launcher profile named '" + packName
                            + "' already exists. Rename packName or delete that profile.");
                }
            }
        }

        String now = Instant.now().toString();
        String id = UUID.randomUUID().toString().replace("-", "");

        JsonObject prof = new JsonObject();
        prof.addProperty("name", packName);
        prof.addProperty("type", "custom");
        prof.addProperty("created", now);
        prof.addProperty("lastUsed", now);
        prof.addProperty("lastVersionId", lastVersionId);
        prof.addProperty("gameDir", gameDir.toAbsolutePath().toString());
        prof.addProperty("icon", "Furnace");

        profiles.add(id, prof);

        Files.writeString(profilesPath, gson.toJson(root), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static void deleteDirectory(Path dir) throws IOException {
        if (!Files.exists(dir))
            return;
        try (var walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private static void replaceDir(Path src, Path dst) throws IOException {
        deleteDirectory(dst);
        Files.createDirectories(dst);
        copyDir(src, dst);
    }

    private static String optString(JsonObject o, String key) {
        return o.has(key) && o.get(key).isJsonPrimitive() ? o.get(key).getAsString() : "";
    }

    private static Path pickGameDir(Path base) throws IOException {
        if (!Files.exists(base))
            return base;
        for (int i = 2; i < 1000; i++) {
            Path p = Paths.get(base.toString() + "-" + i);
            if (!Files.exists(p))
                return p;
        }
        throw new IOException("Could not choose a free gameDir name.");
    }

    private static void copyDir(Path src, Path dst) throws IOException {
        Files.walk(src).forEach(from -> {
            try {
                Path rel = src.relativize(from);
                Path to = dst.resolve(rel);
                if (Files.isDirectory(from)) {
                    Files.createDirectories(to);
                } else {
                    Files.createDirectories(to.getParent());
                    Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private static void copyFile(Path src, Path dst) throws IOException {
        Files.createDirectories(dst.getParent());
        Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void requireFile(Path p, String msg) {
        if (!Files.isRegularFile(p))
            fatal(msg);
    }

    private static void requireDir(Path p, String msg) {
        if (!Files.isDirectory(p))
            fatal(msg);
    }

    private static String javaBin() {
        String home = System.getProperty("java.home");
        Path bin = Paths.get(home, "bin", isWindows() ? "java.exe" : "java");
        return bin.toString();
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    }

    private static Path detectMinecraftDir() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String home = System.getProperty("user.home");

        if (os.contains("win")) {
            String appdata = System.getenv("APPDATA");
            if (appdata != null)
                return Paths.get(appdata, ".minecraft");
        } else if (os.contains("mac")) {
            return Paths.get(home, "Library", "Application Support", "minecraft");
        }
        return Paths.get(home, ".minecraft");
    }

    private static void fatal(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
}
