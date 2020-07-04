package com.gestankbratwurst.avarioncore.resourcepack.packing;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.resourcepack.distribution.ResourcepackManager;
import com.gestankbratwurst.avarioncore.resourcepack.skins.FontMeta;
import com.gestankbratwurst.avarioncore.resourcepack.skins.Model;
import com.gestankbratwurst.avarioncore.resourcepack.skins.ModelBlock;
import com.gestankbratwurst.avarioncore.resourcepack.skins.ModelData;
import com.gestankbratwurst.avarioncore.util.io.ResourceCopy;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.crytec.libs.protocol.skinclient.PlayerSkinManager;
import net.crytec.libs.protocol.skinclient.PlayerSkinManager.ConsumingCallback;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 24.11.2019
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ResourcepackAssembler {

  private static final int META_FORMAT = 5;
  // TODO pack description
  private static final String META_DESC = "- -";

  public ResourcepackAssembler(final AvarionCore plugin) {
    this.plugin = plugin;
    this.clearPluginFolder(plugin);
    this.playerSkinManager = plugin.getUtilModule().getPlayerSkinManager();
    this.packFolderSet = new ObjectLinkedOpenHashSet<>();
    this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    this.skinBackupFile = new File(plugin.getDataFolder(), "skindata.json");
    this.skinCacheFile = new File(plugin.getDataFolder(), "skinchache.json");

    this.packFolderSet.add(this.packFolder = new File(plugin.getDataFolder() + File.separator + "resourcepack"));
    this.packFolderSet.add(this.assetFolder = new File(this.packFolder + File.separator + "assets"));
    this.packFolderSet.add(this.minecraftFolder = new File(this.assetFolder + File.separator + "minecraft"));
    this.packFolderSet.add(this.blockStateFolder = new File(this.minecraftFolder + File.separator + "blockstates"));
    this.packFolderSet.add(this.fontFolder = new File(this.minecraftFolder + File.separator + "font"));
    this.packFolderSet.add(this.langFolder = new File(this.minecraftFolder + File.separator + "lang"));
    this.packFolderSet.add(this.modelsFolder = new File(this.minecraftFolder + File.separator + "models"));
    this.packFolderSet.add(this.itemModelFolder = new File(this.modelsFolder + File.separator + "item"));
    this.packFolderSet.add(this.blockModelFolder = new File(this.modelsFolder + File.separator + "block"));
    this.packFolderSet.add(this.particlesFolder = new File(this.minecraftFolder + File.separator + "particles"));
    this.packFolderSet.add(this.soundsFolder = new File(this.minecraftFolder + File.separator + "sounds" + File.separator + "custom"));
    this.packFolderSet.add(this.texturesFolder = new File(this.minecraftFolder + File.separator + "textures"));
    this.mcmetaFile = new File(this.packFolder, "pack.mcmeta");
    this.soundsFile = new File(this.minecraftFolder, "sounds.json");
    final File stampFolder = new File(plugin.getDataFolder() + File.separator + ResourcepackManager.SERVER_TIMESTAMP);
    stampFolder.mkdirs();
    this.resourceZipFile = new File(stampFolder, "serverpack.zip");
  }

  private final ObjectLinkedOpenHashSet<File> packFolderSet;
  private final AvarionCore plugin;
  private final PlayerSkinManager playerSkinManager;
  private final Gson gson;

  private final File skinBackupFile;
  private final File skinCacheFile;
  private final File resourceZipFile;

  private final File packFolder;
  private final File assetFolder;
  private final File minecraftFolder;
  private final File blockStateFolder;
  private final File fontFolder;
  private final File langFolder;
  private final File modelsFolder;
  private final File itemModelFolder;
  private final File blockModelFolder;
  private final File particlesFolder;
  private final File soundsFolder;
  private final File texturesFolder;

  private final File mcmetaFile;
  private final File soundsFile;

  private void clearPluginFolder(final JavaPlugin plugin) {
    for (final File folder : Objects.requireNonNull(plugin.getDataFolder().listFiles())) {
      if (folder.isDirectory()) {
        final String folderName = folder.getName();
        if (folderName.contains("temp") || folderName.matches("[0-9]+")) {
          try {
            FileUtils.deleteDirectory(folder);
          } catch (final IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  public void zipResourcepack() throws IOException {

    this.setupBaseFiles();

    this.createMetaFile();

    this.compileModels();

    final FileOutputStream fos = new FileOutputStream(this.resourceZipFile);
    final ZipOutputStream zos = new ZipOutputStream(fos);
    this.zipFile(this.assetFolder, this.assetFolder.getName(), zos);
    this.zipFile(this.mcmetaFile, this.mcmetaFile.getName(), zos);

    zos.close();
    fos.close();
  }

  private void createSoundFiles(final File tempFolder) {
    final File tempSoundFolder = new File(tempFolder + File.separator + "sounds");
    if (!tempSoundFolder.exists()) {
      return;
    }
    final JsonObject jsonObject = new JsonObject();
    try {
      for (final File soundTempFile : Objects.requireNonNull(tempSoundFolder.listFiles())) {
        final String fileName = soundTempFile.getName().toLowerCase();
        FileUtils.copyFile(soundTempFile, new File(this.soundsFolder, fileName));
        final JsonObject soundJson = new JsonObject();
        final JsonArray soundList = new JsonArray();
        final String fileStrip = fileName.contains(".") ? fileName.split("\\.")[0] : fileName;
        soundList.add("custom/" + fileStrip);
        soundJson.add("sounds", soundList);
        jsonObject.add("custom." + fileStrip, soundJson);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
    if (!jsonObject.entrySet().isEmpty()) {
      final String data = this.gson.toJson(jsonObject);
      try {
        Files.writeString(this.soundsFile.toPath(), data);
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void compileModels() throws IOException {
    final AssetLibrary assetLibrary = new AssetLibrary(this.plugin);
    final Path temp = Files.createTempDirectory(this.plugin.getDataFolder().toPath(), "temp_rp");
    final File tempFolder = temp.toFile();
    final JsonObject fontJson = new JsonObject();
    final JsonArray providerArray = new JsonArray();
    final char fontIndex = (char) 0x2F00;

    // Textures
    this.exportData(tempFolder);

    // Blocks
    this.loadBlockModels(tempFolder);

    // Items
    this.loadItemModels(tempFolder, fontIndex, providerArray);

    // Skins
    this.createSkinData();

    // TTF
    // TODO fix ttf
    this.createTrueTypeFont(tempFolder, providerArray, fontJson);

    // Json models
    this.createModelJsonFiles(assetLibrary);

    // Sounds
    this.createSoundFiles(tempFolder);

    FileUtils.deleteDirectory(tempFolder);
  }


  private void createMetaFile() throws IOException {
    final OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(this.mcmetaFile));
    osw.write(new PackMeta(META_FORMAT, META_DESC).getAsJsonString());
    osw.close();
  }


  private void exportData(final File tempFolder) {
    try {
      final ResourceCopy copy = new ResourceCopy();
      final JarFile jf = new JarFile(AvarionCore.class.getProtectionDomain().getCodeSource().getLocation().getPath());

      copy.copyResourceDirectory(jf, "resourcepack", tempFolder);
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }


  private void loadBlockModels(final File tempFolder) throws IOException {
    final File modelBlockFolder = new File(tempFolder, "blockstates");
    final Map<String, JsonObject> blockstateJsonMap = Maps.newHashMap();
    for (final ModelBlock modelBlock : ModelBlock.values()) {
      final String vanillaName = modelBlock.getBaseMaterial().getKey().getKey();
      final File modelBlockImage = new File(modelBlockFolder, modelBlock.toString() + ".png");
      if (!modelBlockImage.exists()) {
        this.plugin.getLogger().severe("Could not find image of " + modelBlock.toString());
        continue;
      }

      final JsonObject stateJson;
      if (blockstateJsonMap.containsKey(vanillaName)) {
        stateJson = blockstateJsonMap.get(vanillaName);
      } else {
        stateJson = new JsonObject();
        blockstateJsonMap.put(vanillaName, stateJson);
      }
      final JsonObject variantsJson;
      if (stateJson.has("variants")) {
        variantsJson = stateJson.get("variants").getAsJsonObject();
      } else {
        variantsJson = new JsonObject();
      }
      final JsonObject modelJson = new JsonObject();
      modelJson.addProperty("model", "block/" + modelBlock.toString().toLowerCase());
      variantsJson.add(modelBlock.getBlockStateApplicant(), modelJson);
      stateJson.add("variants", variantsJson);

      final JsonObject customModelJson = new JsonObject();
      customModelJson.addProperty("parent", "block/cube_all");
      final JsonObject textureJson = new JsonObject();
      textureJson.addProperty("all", modelBlock.toString().toLowerCase());
      customModelJson.add("textures", textureJson);
      FileUtils.copyFile(modelBlockImage, new File(this.texturesFolder, modelBlock.toString().toLowerCase() + ".png"));
      final OutputStreamWriter osw = new OutputStreamWriter(
          new FileOutputStream(new File(this.blockModelFolder, modelBlock.toString().toLowerCase() + ".json")), "UTF-8");
      osw.write(this.gson.toJson(customModelJson));
      osw.close();
    }

    for (final Entry<String, JsonObject> entry : blockstateJsonMap.entrySet()) {
      final File stateFile = new File(this.blockStateFolder, entry.getKey() + ".json");
      final OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(stateFile), "UTF-8");
      osw.write(this.gson.toJson(entry.getValue()));
      osw.close();
    }
  }


  private void loadItemModels(final File tempFolder, char fontIndex, final JsonArray providerArray) throws IOException {
    final File textureTempFolder = new File(tempFolder + File.separator + "textures");
    final File imageCache = new File(this.plugin.getDataFolder() + File.separator + "imagecache");
    if (!imageCache.exists()) {
      imageCache.mkdirs();
    }

    // Load static models
    for (final File subFolder : textureTempFolder.listFiles()) {
      for (final File icon : subFolder.listFiles()) {
        final Model model = Model.valueOf(icon.getName().replace(".png", ""));
        final File cachedImage = new File(imageCache, model.toString() + ".png");
        FileUtils.copyFile(icon, cachedImage);
        model.setLinkedImageFile(cachedImage);
        final boolean isBlock = model.getBaseMaterial().isBlock();
        final String nmsName = model.getBaseMaterial().getKey().getKey();
        final File resourceTextureFolder = new File(this.texturesFolder + File.separator + nmsName);
        resourceTextureFolder.mkdirs();
        final File imageFile = new File(resourceTextureFolder, "" + model.getModelID() + ".png");
        FileUtils.copyFile(icon, imageFile);
        final File modNMFolder = isBlock ? this.blockModelFolder : this.itemModelFolder;
        final File resourceModelFolder = new File(modNMFolder + File.separator + nmsName);
        resourceModelFolder.mkdirs();
        final File resourceModelFile = new File(resourceModelFolder, "" + model.getModelID() + ".json");

        final String iconPath = "minecraft:" + nmsName + "/" + model.getModelID() + ".png";
        model.getBoxedFontChar().value = fontIndex;
        final FontMeta fontMeta = model.getFontMeta();
        final JsonObject fontProvider = new JsonObject();
        fontProvider.addProperty("file", iconPath);
        final JsonArray charArray = new JsonArray();
        charArray.add(fontIndex);
        fontProvider.add("chars", charArray);
        fontProvider.addProperty("height", fontMeta.getHeight());
        fontProvider.addProperty("ascent", fontMeta.getAscent());
        fontProvider.addProperty("type", fontMeta.getType());
        providerArray.add(fontProvider);
        fontIndex++;

        final ModelData modelData = model.getModelData();
        if (modelData != null) {
          final JsonObject modelJson = new JsonObject();
          modelJson.addProperty("parent", model.getModelData().getModelParent());
          final JsonObject textureJson = new JsonObject();
          textureJson.addProperty("layer0", nmsName + "/" + model.getModelID());
          modelJson.add("textures", textureJson);

          final JsonObject elementsJson = model.getModelData().getElementsJson();
          if (elementsJson != null) {
            modelJson.add("elements", elementsJson);
          }

          final JsonObject displayJson = model.getModelData().getDisplayJson();
          if (displayJson != null) {
            modelJson.add("display", displayJson);
          }

          final String data = this.gson.toJson(modelJson);
          final OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(resourceModelFile));
          osw.write(data);
          osw.close();
        }
      }
    }

    // Load custom models
    final File customtextureFolder = new File(this.texturesFolder + File.separator + "custom"); // minecraft/textures/custom
    final File tempCustomModelFolder = new File(tempFolder + File.separator + "custommodel"); // intern
    final File rawTextureFolder = new File(tempFolder + File.separator + "raw"); // intern
    final File customTempModelFolder = new File(tempCustomModelFolder + File.separator + "models"); // intern
    final File customTempTextureFolder = new File(tempCustomModelFolder + File.separator + "textures"); // intern
    // itemModelFolder

    // --- --- --- Copy all textures --- --- ---
    if (!customtextureFolder.exists()) {
      customtextureFolder.mkdirs();
    }
    for (final File textureFile : customTempTextureFolder.listFiles()) {
      FileUtils.copyFile(textureFile, new File(customtextureFolder, textureFile.getName()));
    }

    // --- --- --- Copy all model files --- --- ---
    final File customModelFolder = new File(this.itemModelFolder + File.separator + "custom"); // minecraft/models/items/custom
    if (!customModelFolder.exists()) {
      customModelFolder.mkdirs();
    }

    for (final File cModelFileFolder : customTempModelFolder.listFiles()) {
      for (final File cModelFile : cModelFileFolder.listFiles()) {
        Model.valueOf(cModelFile.getName().replace(".json", ""));
        FileUtils.copyFile(cModelFile, new File(customModelFolder, cModelFile.getName().toLowerCase()));
      }
    }

//    for (File raw : rawTextureFolder.listFiles()) {
//      FileUtils.copyFile(raw, new File(itemModelFolder, raw.getName().toLowerCase()));
//    }
  }


  private void createSkinData() throws IOException {
    this.playerSkinManager.loadSkins(this.skinCacheFile);
    final EnumSet<Model> skinlessModels = EnumSet.noneOf(Model.class);
    for (final Model model : Model.values()) {
      if (model.isHeadSkinEnabled()) {
        skinlessModels.add(model);
      }
    }
    JsonObject skinJson = new JsonObject();
    if (this.skinBackupFile.exists()) {
      final InputStreamReader isr = new InputStreamReader(new FileInputStream(this.skinBackupFile));
      final StringBuilder builder = new StringBuilder();
      int read;
      while ((read = isr.read()) != -1) {
        builder.append((char) read);
      }
      skinJson = this.gson.fromJson(builder.toString(), JsonObject.class);
      for (final Entry<String, JsonElement> entry : skinJson.entrySet()) {
        final Model model = Model.valueOf(entry.getKey());
        final int id = entry.getValue().getAsInt();
        final ConsumingCallback callback = this.playerSkinManager.callback(model::setSkin);
        this.playerSkinManager.requestSkin(id, callback);
        while (callback.locked) {
          this.await(250);
        }
        this.await(100);
        skinlessModels.remove(model);
      }
      isr.close();
    }
    final JsonObject skinWriteJson = skinJson;
    final File uploadFolder = new File(this.plugin.getDataFolder() + File.separator + "uploadcache");
    if (!uploadFolder.exists()) {
      uploadFolder.mkdirs();
    }
    for (final Model model : skinlessModels) {
      final File imageFile = model.getLinkedImageFile();
      Preconditions.checkState(imageFile != null);
      final ConsumingCallback callback = this.playerSkinManager.callback(skin -> {
        if (skin != null) {
          skinWriteJson.addProperty(model.toString(), "" + skin.id);
          model.setSkin(skin);
        } else {
          this.plugin.getLogger().warning("Callback on skin is null!");
        }
      });
      this.playerSkinManager.uploadAndScaleHeadImage(imageFile, "AC_MODEL_" + model.toString(), callback);
      while (callback.locked) {
        this.await(250);
      }
      this.await(100);
    }

    final OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(this.skinBackupFile));
    osw.write(this.gson.toJson(skinWriteJson));
    osw.close();

    this.playerSkinManager.cacheSkins(this.skinCacheFile);
  }


  private void createTrueTypeFont(final File tempFolder, final JsonArray providerArray, final JsonObject fontJson) throws IOException {
    final JsonObject ttfProvider = new JsonObject();
    ttfProvider.addProperty("type", "ttf");
    ttfProvider.addProperty("size", 10);
    ttfProvider.addProperty("oversample", 4.5);
    final JsonArray shiftArray = new JsonArray();
    shiftArray.add(0);
    shiftArray.add(1);
    ttfProvider.add("shift", shiftArray);
    ttfProvider.addProperty("file", "minecraft:uniformcenter.ttf");
    // FIXME add ttf back
    //providerArray.add(ttfProvider);

    final File ttfFile = new File(tempFolder, "uniformcenter.ttf");
    FileUtils.copyFile(ttfFile, new File(this.fontFolder, "uniformcenter.ttf"));

    final File tempUnicodeFolder = new File(tempFolder + File.separator + "fontdata");
    final File unicodeFolder = new File(this.texturesFolder + File.separator + "font");
    unicodeFolder.mkdirs();
    for (final File unicodeImage : tempUnicodeFolder.listFiles()) {
      FileUtils.copyFile(unicodeImage, new File(unicodeFolder, unicodeImage.getName()));
    }

    fontJson.add("providers", providerArray);
    final File fontFile = new File(this.fontFolder, "default.json");
    final OutputStreamWriter oswFont = new OutputStreamWriter(new FileOutputStream(fontFile), StandardCharsets.UTF_8);
    oswFont.write(this.gson.toJson(fontJson));
    oswFont.close();
  }


  private void createModelJsonFiles(final AssetLibrary assetLibrary) throws IOException {
    final Map<Material, JsonObject> cachedJsons = Maps.newHashMap();

    for (final Model model : Model.values()) {
      final String nmsName = model.getBaseMaterial().getKey().getKey();
      final boolean isBlock = model.getBaseMaterial().isBlock();
      final JsonArray overrideArray;
      final JsonObject modelObject;

      if (model.getModelData() == null) {
        continue;
      }

      if (!cachedJsons.containsKey(model.getBaseMaterial())) {
        modelObject = new JsonObject();
        if (isBlock) {
          modelObject.addProperty("parent", assetLibrary.getAssetModelParent(nmsName));
        } else {
          modelObject.addProperty("parent", assetLibrary.getAssetModelParent(nmsName));
          final JsonObject textureObject = new JsonObject();
          textureObject.addProperty("layer0", assetLibrary.getAssetModelLayer0(nmsName));
          modelObject.add("textures", textureObject);
        }

        overrideArray = new JsonArray();
        modelObject.add("overrides", overrideArray);

      } else {
        modelObject = cachedJsons.get(model.getBaseMaterial());
        overrideArray = modelObject.get("overrides").getAsJsonArray();
      }

      final JsonObject overrideObject = new JsonObject();
      final String customModelName;
      if (model.isCustomModelDataEnabled()) {
        customModelName = assetLibrary.getAssetModelLayer0(nmsName).split("/")[0] + "/custom/" + model.toString().toLowerCase();
      } else {
        customModelName = assetLibrary.getAssetModelLayer0(nmsName).split("/")[0] + "/" + nmsName + "/" + model.getModelID();
      }
      overrideObject.addProperty("model", customModelName);
      final JsonObject predicateObject = new JsonObject();
      predicateObject.addProperty("custom_model_data", model.getModelID());
      overrideObject.add("predicate", predicateObject);
      overrideArray.add(overrideObject);

      modelObject.add("overrides", overrideArray);
      cachedJsons.put(model.getBaseMaterial(), modelObject);
    }

    for (final Model model : Model.values()) {
      if (model.getModelData() != null) {
        final File modelFolder = model.getBaseMaterial().isBlock() ? this.blockModelFolder : this.itemModelFolder;
        final File modelFile = new File(modelFolder, model.getBaseMaterial().getKey().getKey() + ".json");
        final OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(modelFile));
        osw.write(this.gson.toJson(cachedJsons.get(model.getBaseMaterial())));
        osw.close();
      }
    }
  }


  private void setupBaseFiles() throws IOException {
    FileUtils.deleteDirectory(this.assetFolder);
    for (final File folder : this.packFolderSet) {
      folder.mkdirs();
    }
  }


  private void zipFile(final File fileToZip, final String fileName, final ZipOutputStream zipOut) throws IOException {
    if (fileToZip.isHidden()) {
      return;
    }
    if (fileToZip.isDirectory()) {
      if (fileName.endsWith("/")) {
        zipOut.putNextEntry(new ZipEntry(fileName));
        zipOut.closeEntry();
      } else {
        zipOut.putNextEntry(new ZipEntry(fileName + "/"));
        zipOut.closeEntry();
      }
      final File[] children = fileToZip.listFiles();
      for (final File childFile : children) {
        this.zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
      }
      return;
    }
    final FileInputStream fis = new FileInputStream(fileToZip);
    final ZipEntry zipEntry = new ZipEntry(fileName);
    zipOut.putNextEntry(zipEntry);
    final byte[] bytes = new byte[1024];
    int length;
    while ((length = fis.read(bytes)) >= 0) {
      zipOut.write(bytes, 0, length);
    }
    fis.close();
  }


  private void await(final long ms) {
    try {
      Thread.sleep(ms);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
  }


}
