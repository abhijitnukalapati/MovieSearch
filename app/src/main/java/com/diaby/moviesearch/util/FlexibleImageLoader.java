package com.diaby.moviesearch.util;

import android.content.Context;

import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.diaby.moviesearch.model.MConfiguration;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by abhijitnukalapati on 11/14/16.
 */

public class FlexibleImageLoader extends BaseGlideUrlLoader<String>{

    public enum IMAGE_TYPE {
        POSTER,
        BACKDROP
    }

    private static final Pattern WIDTH_PATTERN = Pattern.compile("w(\\d+)");
    private Context context;
    private IMAGE_TYPE type;

    public FlexibleImageLoader(Context context, IMAGE_TYPE type) {
        super(context);
        this.context = context.getApplicationContext();
        this.type = type;
    }

    @Override
    protected String getUrl(String path, int width, int height) {
        MConfiguration configuration = ConfigurationUtil.getInstance(context).retrieveConfig();

        if(configuration == null || configuration.getImages() == null || path == null) {
            return "";
        }

        MConfiguration.Images images = configuration.getImages();

        List<String> widthStrings;
        if(type == IMAGE_TYPE.POSTER) {
            widthStrings = images.getPosterSizes();
        } else{
            widthStrings = images.getBackdropSizes();
        }

        int resultIndex = widthStrings.size() - 1; // original

        for(int i = 0; i < widthStrings.size() - 1; i++) {
            Matcher m = WIDTH_PATTERN.matcher(widthStrings.get(i));
            if (m.find()) {
                int value = Integer.parseInt(m.group(1));
                if(width <= value) {
                    resultIndex = i;
                    break;
                }
            }
        }

        String url = images.getSecureBaseUrl() + widthStrings.get(resultIndex) + path;
        return url;
    }
}
