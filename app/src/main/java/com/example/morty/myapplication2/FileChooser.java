package com.example.morty.myapplication2;


import android.app.Activity;
import android.app.Dialog;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

public class FileChooser {
    private static final String PARENT_DIR = "..";

    private final Activity activity;
    private ListView list;
    private Dialog dialog;
    private File currentPath;

    // filter on file extension
    private String extension = null;
    public void setExtension(String extension) {
        this.extension = (extension == null) ? null :
                extension.toLowerCase();
    }

    // file selection event handling
    public interface FileSelectedListener {
        void fileSelected(File file);
    }
    public FileChooser setFileListener(FileSelectedListener fileListener) {
        this.fileListener = fileListener;
        return this;
    }
    private FileSelectedListener fileListener;

    public FileChooser(Activity activity) {
        this.activity = activity;
        dialog = new Dialog(activity);
        list = new ListView(activity);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int which, long id) {
                String fileChosen = (String) list.getItemAtPosition(which);
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    refresh(chosenFile);
                } else {
                    if (fileListener != null) {
                        fileListener.fileSelected(chosenFile);
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.setContentView(list);
        dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        refresh(Environment.getExternalStorageDirectory());
    }

    public void showDialog() {
        dialog.show();
    }


    /**
     * Sort, filter and display the files for the given path.
     */
    private void refresh(File path) {
        this.currentPath = path;
        if (path.exists()) {
            if (path.exists() && path.canRead()) {
                // find em all
                TreeSet<String> dirs = new TreeSet<>();
                TreeSet<String> files = new TreeSet<>();
                for (File file : path.listFiles()) {
                    if (!file.canRead())
                        continue;
                    if (file.isDirectory()) {
                        dirs.add(file.getName());
                    } else {
                        if (extension == null || file.getName().toLowerCase().endsWith(extension))
                            files.add(file.getName());
                    }
                }

                // convert to an array
                ArrayList<String> fileList = new ArrayList<>(dirs.size() + files.size());
                if (path.getParentFile() != null)
                    fileList.add(PARENT_DIR);
                fileList.addAll(dirs);
                fileList.addAll(files);

                // refresh the user interface
                dialog.setTitle(currentPath.getPath());
                list.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, fileList) {
                    @Override
                    public View getView(int pos, View view, ViewGroup parent) {
                        view = super.getView(pos, view, parent);
                        ((TextView) view).setSingleLine(true);
                        return view;
                    }
                });
            } else {
                list.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, new String[]{"Can't access " + path}));
            }

        }
    }
    /**
     * Convert a relative filename into an actual File object.
     */
    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) {
            return currentPath.getParentFile();
        } else {
            return new File(currentPath, fileChosen);
        }
    }
}
