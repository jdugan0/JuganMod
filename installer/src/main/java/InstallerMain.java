import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import com.google.gson.*;

public final class InstallerMain {

    public static void main(String[] args) throws Exception {
        String packName = "JuganSMP";
        String packId = "jugansmp";
        String mcVersion = "1.21.11";
        String loaderVersion = "0.18.2";

        Path workDir = Paths.get("").toAbsolutePath();
        Path fabricInstaller = workDir.resolve("fabric-installer.jar");
        Path modsSrc = workDir.resolve("mods");
        Path configSrc = workDir.resolve("config");
        Path rpSrc = workDir.resolve("resourcepacks");
        Path optionsSrc = workDir.resolve("options.txt");

        requireFile(fabricInstaller, "Missing fabric-installer.jar");
        requireDir(modsSrc, "Missing mods folder");

        Path mcDir = detectMinecraftDir();
        requireDir(mcDir, "Minecraft directory not found");

        runFabricInstallerNoProfile(fabricInstaller, mcDir, mcVersion, loaderVersion);

        Path gameDir = mcDir.resolve(packId);

        deleteDirectory(gameDir);
        Files.createDirectories(gameDir);

        replaceDir(modsSrc, gameDir.resolve("mods"));
        if (Files.isDirectory(configSrc))
            replaceDir(configSrc, gameDir.resolve("config"));
        if (Files.isDirectory(rpSrc))
            replaceDir(rpSrc, gameDir.resolve("resourcepacks"));
        if (Files.isRegularFile(optionsSrc))
            copyFile(optionsSrc, gameDir.resolve("options.txt"));

        String lastVersionId = "fabric-loader-" + loaderVersion + "-" + mcVersion;
        upsertLauncherProfile(mcDir, packName, lastVersionId, gameDir);

        System.out.println("Install complete. Existing install was fully replaced.");
    }

    private static void runFabricInstallerNoProfile(Path installerJar, Path mcDir,
            String mcVersion, String loaderVersion) throws Exception {

        List<String> cmd = List.of(
                javaBin(),
                "-jar", installerJar.toString(),
                "client",
                "-dir", mcDir.toString(),
                "-mcversion", mcVersion,
                "-loader", loaderVersion,
                "-noprofile");

        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            while (br.readLine() != null) {}
        }

        if (p.waitFor() != 0)
            fatal("Fabric installer failed");
    }

    private static void upsertLauncherProfile(Path mcDir, String name,
            String lastVersionId, Path gameDir) throws IOException {

        Path profilesPath = mcDir.resolve("launcher_profiles.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject root = Files.exists(profilesPath)
                ? JsonParser.parseString(Files.readString(profilesPath)).getAsJsonObject()
                : new JsonObject();

        JsonObject profiles = root.has("profiles")
                ? root.getAsJsonObject("profiles")
                : new JsonObject();
        root.add("profiles", profiles);

        String id = null;
        for (var e : profiles.entrySet()) {
            JsonObject p = e.getValue().getAsJsonObject();
            if (name.equals(p.get("name").getAsString())) {
                id = e.getKey();
                break;
            }
        }

        if (id == null)
            id = UUID.randomUUID().toString().replace("-", "");

        String now = Instant.now().toString();
        JsonObject prof = new JsonObject();
        prof.addProperty("name", name);
        prof.addProperty("type", "custom");
        prof.addProperty("created", now);
        prof.addProperty("lastUsed", now);
        prof.addProperty("lastVersionId", lastVersionId);
        prof.addProperty("gameDir", gameDir.toAbsolutePath().toString());
        prof.addProperty("icon", "Furnace");

        profiles.add(id, prof);

        Files.writeString(profilesPath, gson.toJson(root),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static void deleteDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        try (var walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try { Files.delete(p); }
                    catch (IOException e) { throw new UncheckedIOException(e); }
                });
        }
    }

    private static void replaceDir(Path src, Path dst) throws IOException {
        deleteDirectory(dst);
        Files.createDirectories(dst);
        copyDir(src, dst);
    }

    private static void copyDir(Path src, Path dst) throws IOException {
        Files.walk(src).forEach(from -> {
            try {
                Path to = dst.resolve(src.relativize(from));
                if (Files.isDirectory(from))
                    Files.createDirectories(to);
                else {
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
        if (!Files.isRegularFile(p)) fatal(msg);
    }

    private static void requireDir(Path p, String msg) {
        if (!Files.isDirectory(p)) fatal(msg);
    }

    private static String javaBin() {
        Path bin = Paths.get(System.getProperty("java.home"),
                "bin", isWindows() ? "java.exe" : "java");
        return bin.toString();
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private static Path detectMinecraftDir() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");
        if (os.contains("win")) {
            String app = System.getenv("APPDATA");
            if (app != null) return Paths.get(app, ".minecraft");
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
