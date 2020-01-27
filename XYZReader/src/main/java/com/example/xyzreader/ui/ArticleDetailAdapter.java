package com.example.xyzreader.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.Article;


//adapter class
// https://guides.codepath.com/android/Heterogenous-Layouts-inside-RecyclerView#overview
public class ArticleDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Article articleElements;

    private final int IMAGE = 0, TITLE = 1, BODY = 2;

    private Context context;

    private int mMutedColor = 0xFF333333;


    ArticleDetailAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case IMAGE:
                View v1 = inflater.inflate(R.layout.list_item_detail_image, viewGroup, false);
                viewHolder = new ArticleImageViewHolder(v1);
                break;
            case TITLE:
                View v2 = inflater.inflate(R.layout.list_item_detail_title, viewGroup, false);
                viewHolder = new ArticleTitleViewHolder(v2);
                break;
            default: // body text
                View v = inflater.inflate(R.layout.list_item_detail_body, viewGroup, false);
                viewHolder = new ArticleBodyViewHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case IMAGE:
                ArticleImageViewHolder vhImage = (ArticleImageViewHolder) viewHolder;
                configureImageViewHolder(vhImage);
                break;
            case TITLE:
                ArticleTitleViewHolder vhTitle = (ArticleTitleViewHolder) viewHolder;
                configureTitleViewHolder(vhTitle);
                break;
            default:
                ArticleBodyViewHolder vhBody = (ArticleBodyViewHolder) viewHolder;
                configureTextBodyViewHolder(vhBody, position);
                break;
        }
    }

    private void configureTextBodyViewHolder(ArticleBodyViewHolder vhBody, int position) {
        String[] bodyPart = articleElements.getArticleBody();
        String singleTextElement = bodyPart[position - 2];
        vhBody.articleBodyTextView.setText(Html.fromHtml(singleTextElement));
    }

    private void configureTitleViewHolder(ArticleTitleViewHolder vhTitle) {
        vhTitle.articleTitleTextView.setText(Html.fromHtml(articleElements.getTitle()));
        vhTitle.articleBylineTextView.setText(Html.fromHtml(articleElements.getByline()));
        vhTitle.metaBar.setBackgroundColor(mMutedColor);
    }

    private void configureImageViewHolder(ArticleImageViewHolder vhImage) {
        final ImageView mPhotoView = vhImage.articleImage;
        ImageLoaderHelper.getInstance(context).getImageLoader()
                .get(articleElements.getImagePath(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Bitmap bitmap = imageContainer.getBitmap();
                        if (bitmap != null) {
                            Palette p = Palette.generate(bitmap, 12);
                            mMutedColor = p.getDarkMutedColor(0xFF333333);
                            mPhotoView.setImageBitmap(imageContainer.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case IMAGE:
            case TITLE:
                return position;
        }
        return BODY;
    }

    @Override
    public int getItemCount() {
        if (articleElements != null) {
            Log.i("Adapter", "item count: " + articleElements.getArticleBody().length + 2);
            return articleElements.getArticleBody().length + 2; // text body + image + title (title+byline)
        }
        return 0;
    }

    void setArticleData(Article articleElements) {
        this.articleElements = articleElements;
        notifyDataSetChanged();
    }

    class ArticleImageViewHolder extends RecyclerView.ViewHolder {

        final ImageView articleImage;
        final FrameLayout photoContainer;

        ArticleImageViewHolder(View view) {
            super(view);
            articleImage = (ImageView) view.findViewById(R.id.article_detail_image);
            photoContainer = view.findViewById(R.id.photo_container);
        }

    }

    class ArticleTitleViewHolder extends RecyclerView.ViewHolder {

        final TextView articleTitleTextView;
        final TextView articleBylineTextView;
        final LinearLayout metaBar;

        ArticleTitleViewHolder(View view) {
            super(view);
            articleTitleTextView = (TextView) view.findViewById(R.id.article_detail_title);
            articleBylineTextView = (TextView) view.findViewById(R.id.article_detail_byline);
            metaBar = view.findViewById(R.id.meta_bar);
            articleBylineTextView.setMovementMethod(new LinkMovementMethod());
        }

    }

    class ArticleBodyViewHolder extends RecyclerView.ViewHolder {

        final TextView articleBodyTextView;

        ArticleBodyViewHolder(View view) {
            super(view);
            articleBodyTextView = (TextView) view.findViewById(R.id.article_body_text);
        }

    }
}
