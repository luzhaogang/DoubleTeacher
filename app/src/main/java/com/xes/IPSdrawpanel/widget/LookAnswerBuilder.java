package com.xes.IPSdrawpanel.widget;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.xes.IPSdrawpanel.R;
import com.xes.IPSdrawpanel.bean.ProblemBank;
import com.xes.IPSdrawpanel.util.Player;
import com.xes.IPSdrawpanel.util.RichText;
import com.xes.IPSdrawpanel.util.Utility;


public class LookAnswerBuilder {
	private Player questionPlayer;
	private Activity context;
	private ProblemBank pb;
	private View Lookview;
	public DrawableCenterTextView main_questionAnswer;
	
	public LookAnswerBuilder(Activity context,ProblemBank pb,DrawableCenterTextView main_questionAnswer){
		this.context =context;
		
		this.pb = pb;
		this.main_questionAnswer = main_questionAnswer;		
	}

	    public  void seeAnswer() {
		final MaterialDialog	mMaterialDialog = new MaterialDialog(context);
		mMaterialDialog.setBackgroundResource(android.R.color.transparent);
		Lookview = LayoutInflater.from(context).inflate(R.layout.seeanswer, null);
		mMaterialDialog.setView(Lookview).show();
		RichText question = (RichText) Lookview.findViewById(R.id.question_content);
		RichText questionImage = (RichText) Lookview.findViewById(R.id.question_content1);
		TextView optionA = (TextView) Lookview.findViewById(R.id.option_a);
		TextView optionB = (TextView) Lookview.findViewById(R.id.option_b);
		TextView optionC = (TextView) Lookview.findViewById(R.id.option_c);
		TextView optionD = (TextView) Lookview.findViewById(R.id.option_d);
		TextView answer = (TextView) Lookview.findViewById(R.id.answer);
		RichText answer_tv = (RichText) Lookview.findViewById(R.id.answer_tv);
		TextView analysis = (TextView) Lookview.findViewById(R.id.analysis);
		RichText analysis_tv = (RichText) Lookview.findViewById(R.id.analysis_tv);
		final Button questionSay = (Button) Lookview.findViewById(R.id.questionSay);
		TextView text_questionSay = (TextView) Lookview.findViewById(R.id.text_questionSay);
		questionSay.setTag("1");
		final SeekBar questionSay_progress = (SeekBar) Lookview.findViewById(R.id.questionSay_progress);
		questionSay_progress.setVisibility(View.GONE);
		Button diss_window = (Button) Lookview.findViewById(R.id.dissmiss);
		diss_window.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (questionPlayer != null) {
					questionSay.setTag("1");
					Drawable ptopDrawable = Utility.getCenterDrawable(R.drawable.pause_listen, context);
					questionSay.setBackgroundDrawable(ptopDrawable);
					questionSay_progress.setVisibility(View.INVISIBLE);
					questionPlayer.stop();
					questionPlayer = null;
				}
				mMaterialDialog.dismiss();
				main_questionAnswer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct_icon_answer, 0, 0, 0);
				main_questionAnswer.setTextColor(context.getResources().getColor(R.color.dark_grey_text));

			}
		});
		if (!TextUtils.isEmpty(pb.analysisAudioUrl) && pb.analysisAudioUrl.length() > 4 && pb.analysisAudioUrl.substring(pb.analysisAudioUrl.length() - 4, pb.analysisAudioUrl.length()).equals(".amr")) {
			questionSay.setVisibility(View.VISIBLE);
			text_questionSay.setVisibility(View.VISIBLE);
		}else{
			questionSay.setVisibility(View.INVISIBLE);
			text_questionSay.setVisibility(View.INVISIBLE);
		}
		
		questionSay.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				if (!TextUtils.isEmpty(pb.analysisAudioUrl) && pb.analysisAudioUrl.length() > 4 && pb.analysisAudioUrl.substring(pb.analysisAudioUrl.length() - 4, pb.analysisAudioUrl.length()).equals(".amr")) {
					if (questionSay.getTag().equals("1")) {
						questionSay.setTag("2");
						if (questionPlayer == null) {
							questionPlayer = new Player(questionSay_progress, questionSay, context);
						} else {
							questionPlayer.stop();
							questionPlayer = null;
							questionPlayer = new Player(questionSay_progress, questionSay, context);
						}

						Drawable centerDrawable = Utility.getCenterDrawable(R.drawable.pause_listen, context);
						questionSay.setBackgroundDrawable(centerDrawable);
						questionSay_progress.setVisibility(View.VISIBLE);

						questionSay_progress.setOnSeekBarChangeListener(new QuestionSeekBarChangeEvent());

						new Thread(new Runnable() {

							@Override
							public void run() {
								questionPlayer.playUrl(pb.analysisAudioUrl);
							}
						}).start();

					} else {
						questionSay.setTag("1");
						Drawable ptopDrawable = Utility.getCenterDrawable(R.drawable.listen_record_button, context);
						questionSay.setBackgroundDrawable(ptopDrawable);
						questionSay_progress.setVisibility(View.INVISIBLE);
						questionPlayer.stop();
						questionPlayer = null;
					}

				} else {
					Utility.showToast(context, "无语音解析");
				}
			}
		});
		if (pb.getType() != 1 || pb.getType() != 2) {
			optionA.setVisibility(View.GONE);
			optionB.setVisibility(View.GONE);
			optionC.setVisibility(View.GONE);
			optionD.setVisibility(View.GONE);
		} else {
			optionA.setVisibility(View.VISIBLE);
			optionB.setVisibility(View.VISIBLE);
			optionC.setVisibility(View.VISIBLE);
			optionD.setVisibility(View.VISIBLE);
		}
		question.setRichText(pb.getTitle(),Lookview);
		if (!pb.contentImg.isEmpty()) {
			questionImage.setRichText(pb.getContentImg(), questionImage);
		} else {
			questionImage.setVisibility(View.GONE);
		}
		if (pb.getAnswer() != null && !pb.getAnswer().isEmpty()) {
			answer_tv.setRichText(pb.getAnswer(),Lookview);
		} else {
			answer_tv.setVisibility(View.GONE);
			answer.setVisibility(View.GONE);
		}
		if (pb.getAnalysis() != null && !pb.getAnalysis().isEmpty()) {
			analysis_tv.setRichText(pb.getAnalysis(),Lookview);
		} else {
			analysis_tv.setVisibility(View.GONE);
			analysis.setVisibility(View.GONE);
		}
	}
	
	class QuestionSeekBarChangeEvent implements OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
			this.progress = progress * questionPlayer.mediaPlayer.getDuration() / seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
			questionPlayer.mediaPlayer.seekTo(progress);
		}
	}
}
