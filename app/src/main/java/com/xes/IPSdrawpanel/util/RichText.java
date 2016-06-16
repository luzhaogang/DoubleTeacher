package com.xes.IPSdrawpanel.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.VolleyError;
import com.xes.IPSdrawpanel.R;
import com.xes.IPSdrawpanel.util.ImageHttpUtile.RequestListener;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

public class RichText extends TextView {

	private Drawable placeHolder, errorImage;// 占位图，错误图
	private OnImageClickListener onImageClickListener;// 图片点击回调
	private Context context;
	private View view;

	public RichText(Context context) {
		this(context, null);
		this.context = context;
	}

	public RichText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.context = context;
	}

	public RichText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RichText);
		placeHolder = typedArray.getDrawable(R.styleable.RichText_placeHolder);
		errorImage = typedArray.getDrawable(R.styleable.RichText_errorImage);

		if (placeHolder == null) {
			placeHolder = new ColorDrawable(Color.GRAY);
		}
		if (errorImage == null) {
			errorImage = new ColorDrawable(Color.GRAY);
		}
		typedArray.recycle();
	}

	/**
	 * 设置富文本
	 * 
	 * @param text
	 *            富文本
	 */
	public void setRichText(String text, View view) {
		this.view = view;
		Spanned spanned = Html.fromHtml(text, asyncImageGetter, null);
		SpannableStringBuilder spannableStringBuilder;
		if (spanned instanceof SpannableStringBuilder) {
			spannableStringBuilder = (SpannableStringBuilder) spanned;
		} else {
			spannableStringBuilder = new SpannableStringBuilder(spanned);
		}

		ImageSpan[] imageSpans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ImageSpan.class);
		final List<String> imageUrls = new ArrayList<String>();

		for (int i = 0, size = imageSpans.length; i < size; i++) {
			ImageSpan imageSpan = imageSpans[i];
			String imageUrl = imageSpan.getSource();
			int start = spannableStringBuilder.getSpanStart(imageSpan);
			int end = spannableStringBuilder.getSpanEnd(imageSpan);
			imageUrls.add(imageUrl);

			final int finalI = i;
			ClickableSpan clickableSpan = new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					if (onImageClickListener != null) {
						onImageClickListener.imageClicked(imageUrls, finalI);
					}
				}
			};
			ClickableSpan[] clickableSpans = spannableStringBuilder.getSpans(start, end, ClickableSpan.class);
			if (clickableSpans != null && clickableSpans.length != 0) {
				for (ClickableSpan cs : clickableSpans) {
					spannableStringBuilder.removeSpan(cs);
				}
			}
			spannableStringBuilder.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		super.setText(spanned);
		setMovementMethod(LinkMovementMethod.getInstance());
	}

	/**
	 * 异步加载图片
	 */
	private Html.ImageGetter asyncImageGetter = new Html.ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			final URLDrawable urlDrawable = new URLDrawable();
			if ("http".equals(source.trim().substring(0, 4))) {
				ImageHttpUtile.getImageBitmap(source, new RequestListener() {
					@Override
					public void onSuccess(Bitmap bitmap) {
						Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
						//24为xml配置文件 mar的值
						int screenWidth = view.getWidth()-DensityUtil.dip2px(24);
						float scale = 2;
						if (getScreenPixel(context).widthPixels <= 480) {
							scale = 1;
						} else if (getScreenPixel(context).widthPixels <= 720) {
							scale = 2;
						} else if (getScreenPixel(context).widthPixels <= 1080) {
							scale = 3;
						}
						if(bitmap.getWidth()*scale >= screenWidth){
							float scalew = screenWidth / (bitmap.getWidth()*scale);
							drawable.setBounds(0, 0, Math.round(drawable.getIntrinsicWidth() * scale*scalew), Math.round(drawable.getIntrinsicHeight() * scale*scalew));
							urlDrawable.setBounds(0, 0, Math.round(drawable.getIntrinsicWidth() * scale*scalew), Math.round(drawable.getIntrinsicHeight() * scale*scalew));
							urlDrawable.setDrawable(drawable);
						}else{
							drawable.setBounds(0, 0, Math.round(drawable.getIntrinsicWidth() * scale), Math.round(drawable.getIntrinsicHeight() * scale));
							urlDrawable.setBounds(0, 0, Math.round(drawable.getIntrinsicWidth() * scale), Math.round(drawable.getIntrinsicHeight() * scale));
							urlDrawable.setDrawable(drawable);
						}
						RichText.this.setText(getText());

					}

					@Override
					public void onError(VolleyError errorMsg) {
						urlDrawable.setBounds(errorImage.getBounds());
						urlDrawable.setDrawable(errorImage);

					}
				});
			} else {
				Bitmap image = null;
				AssetManager am = context.getResources().getAssets();
				try {

					InputStream is = am.open("img/" + source);
					image = BitmapFactory.decodeStream(is);
					is.close();
					Drawable drawable = new BitmapDrawable(getContext().getResources(), image);
					drawable.setBounds(0, 0, image.getWidth(), image.getHeight());
					urlDrawable.setBounds(0, 0, image.getWidth(), image.getHeight());
					urlDrawable.setDrawable(drawable);
					RichText.this.setText(getText());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return urlDrawable;
		}
	};

	public static class URLDrawable extends BitmapDrawable {
		private Drawable drawable;

		@SuppressWarnings("deprecation")
		public URLDrawable() {
			drawable = new ColorDrawable(Color.GRAY);
		}

		@Override
		public void draw(Canvas canvas) {
			if (drawable != null)
				drawable.draw(canvas);
		}

		public void setDrawable(Drawable drawable) {
			this.drawable = drawable;
		}
	}

	public void setPlaceHolder(Drawable placeHolder) {
		this.placeHolder = placeHolder;
	}

	public void setErrorImage(Drawable errorImage) {
		this.errorImage = errorImage;
	}

	public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
		this.onImageClickListener = onImageClickListener;
	}

	public interface OnImageClickListener {
		/**
		 * 图片被点击后的回调方法
		 * 
		 * @param imageUrls
		 *            本篇富文本内容里的全部图片
		 * @param position
		 *            点击处图片在imageUrls中的位置
		 */
		void imageClicked(List<String> imageUrls, int position);
	}

	public static DisplayMetrics getScreenPixel(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}
}
