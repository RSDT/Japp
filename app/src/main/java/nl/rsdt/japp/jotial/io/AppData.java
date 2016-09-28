package nl.rsdt.japp.jotial.io;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import nl.rsdt.japp.application.Japp;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-1-2016
 * Class that handles all saving and loading operations.
 */
public class AppData {

    /**
     * The directory the AppData object should operate in.
     * */
    private static File fDir;

    /**
     * Initializes the AppData object.
     *
     * @param dir The directory the AppData object should operate in.
     * */
    public static void initialize(File dir)
    {
        fDir = dir;
    }

    /**
     * Checks if the save exists.
     *
     * @param filename The name of the file where the save should be.
     * */
    public static boolean hasSave(String filename)
    {
        return new File(fDir, filename).exists();
    }

    /**
     * Saves a object in Json format.
     *
     * @param object The object that should be saved.
     * @param filename The name of the file where the object should be saved on.
     * */
    public static void saveObjectAsJson(Object object, String filename)
    {
        new SaveTask(object, filename).run();
    }

    /**
     * Saves a object in Json format in the background.
     *
     * @param object The object that should be saved.
     * @param filename The name of the file where the object should be saved on.
     * */
    public static void saveObjectAsJsonInBackground(Object object, String filename)
    {
        new Thread(new SaveTask(object, filename)).start();
    }

    /**
     * Saves a Drawable in a file.
     *
     * @param drawable The Drawable that should be saved.
     * @param filename The name of the file where the Drawable should be saved on.
     * */
    public static void saveDrawable(Drawable drawable, String filename)
    {
        new SaveDrawableTask(drawable, filename).run();
    }

    /**
     * Saves a Drawable in a file in the background.
     *
     * @param drawable The Drawable that should be saved.
     * @param filename The name of the file where the Drawable should be saved on.
     * */
    public static void saveDrawableInBackground(Drawable drawable, String filename)
    {
        new Thread(new SaveDrawableTask(drawable, filename)).start();
    }

    public static boolean delete(String filename) {
        File file = new File(fDir, filename);
        return file.delete();
    }

    /**
     * Gets a object out of the save.
     *
     * @param filename The name of the file where the object is stored.
     * @param type The type of the object.
     * */
    @Nullable
    public static <T> T getObject(String filename, Type type)
    {
        if(hasSave(filename)) {
            try
            {
                File file = new File(fDir, filename);
                if(file.exists())
                {
                    JsonReader jsonReader = new JsonReader(new FileReader(file));
                    jsonReader.setLenient(true);
                    return new Gson().fromJson(jsonReader, type);
                }
                return null;
            }
            catch(Exception e)
            {
                Log.e("AppData", e.toString(), e);
            }
        } else {
            Log.e("AppData", "File was not found");
        }

        return null;
    }

    /**
     * Gets the Drawable out of the save.
     *
     * @param filename The name of the file where the Drawable is stored.
     * */
    @Nullable
    public static Drawable getDrawable(String filename)
    {
        try
        {
            File file = new File(fDir, filename);
            return new BitmapDrawable(BitmapFactory.decodeStream(new FileInputStream(file)));
        }
        catch (Exception e)
        {
            Log.e("AppData", e.toString(), e);
        }
        return null;
    }

    public static void clear() {
        /**
         * Clear data files.
         * */
        File dir = fDir;
        if(dir.exists() && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 29-1-2016
     * Class for saving a object.
     */
    public static class SaveTask implements Runnable {

        /**
         * The object that is going to be saved.
         * */
        private Object object;

        /**
         * The name of the file where the object is going to be saved.
         * */
        private String filename;

        /**
         * Initializes a new instance of SaveTask.
         * */
        public SaveTask(Object object, String filename)
        {
            this.object = object;
            this.filename = filename;
        }

        @Override
        public void run() {
            try
            {
                File file = new File(fDir, filename);
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(new Gson().toJson(object));
                fileWriter.flush();
                fileWriter.close();
            }
            catch(Exception e)
            {
                Log.e("AppData", "Error occured", e);
            }
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 12-2-2016
     * Class for saving a drawable.
     */
    public static class SaveDrawableTask implements Runnable
    {

        /**
         * The Drawable to save.
         * */
        private Drawable drawable;

        /**
         * The name of the file where the Drawable should be saved.
         * */
        private String filename;

        /**
         * Initializes a new instance of SaveDrawableTask.
         *
         * @param drawable The Drawable to save.
         * @param filename The name of the file where you want to save the Drawable.
         * */
        public SaveDrawableTask(Drawable drawable, String filename)
        {
            this.drawable = drawable;
            this.filename = filename;
        }

        @Override
        /**
         * Saved the Drawable to the given file.
         * */
        public void run() {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(new File(fDir, filename));
                ((BitmapDrawable)drawable).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}
