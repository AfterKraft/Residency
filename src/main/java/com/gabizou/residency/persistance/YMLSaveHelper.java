package com.gabizou.residency.persistance;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.ReaderException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class YMLSaveHelper {

    File f;
    Yaml yml;
    Map<String, Object> root;

    public YMLSaveHelper(File ymlfile) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        options.setAllowUnicode(true);
        this.yml = new Yaml(options);

        this.root = new LinkedHashMap<String, Object>();
        if (ymlfile == null) {
            throw new IOException("YMLSaveHelper: null file...");
        }
        this.f = ymlfile;
    }

    public void save() throws IOException {
        if (this.f.isFile()) {
            this.f.delete();
        }
        FileOutputStream fout = new FileOutputStream(this.f);
        OutputStreamWriter osw = new OutputStreamWriter(fout, "UTF8");
        this.yml.dump(this.root, osw);
        osw.close();
    }

    @SuppressWarnings("unchecked")
    public void load() throws IOException {
        InputStream fis = new FileInputStream(this.f);
        try {
            this.root = (Map<String, Object>) this.yml.load(fis);
        } catch (ReaderException e) {
            System.out.println("[Residence] - Failed to load " + this.yml.getName() + " file!");
        }
        fis.close();
    }

    public Map<String, Object> getRoot() {
        return this.root;
    }
}
