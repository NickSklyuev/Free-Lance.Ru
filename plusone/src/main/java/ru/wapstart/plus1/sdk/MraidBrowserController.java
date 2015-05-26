package ru.wapstart.plus1.sdk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

class MraidBrowserController extends MraidAbstractController {
	private static final String LOGTAG = "MraidBrowserController";

	MraidBrowserController(MraidView view) {
		super(view);
	}

	protected void open(String url) {
		Log.d(LOGTAG, "Opening in-app browser: " + url);

		MraidView view = getView();
		if (view.getOnOpenListener() != null) {
			view.getOnOpenListener().onOpen(view);
		}

		Context context = getView().getContext();
		Intent i = new Intent(context, ApplicationBrowser.class);
		i.putExtra(ApplicationBrowser.URL_EXTRA, url);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
}
