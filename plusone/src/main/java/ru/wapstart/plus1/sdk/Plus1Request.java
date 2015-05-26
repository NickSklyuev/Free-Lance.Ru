/**
 * Copyright (c) 2010, Alexander Klestov <a.klestov@co.wapstart.ru>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the "Wapstart" nor the names
 *     of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ru.wapstart.plus1.sdk;

import android.location.Location;
import android.net.Uri;
import android.os.Build;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public final class Plus1Request {
	private static final Integer REQUEST_VERSION = 3;

	public static enum Gender {Unknown, Male, Female};
	public static enum BannerType {Undefined, Mixed, Text, Graphic, RichMedia};
	public static enum RequestType {xml, json, html, js, init};

	private String serverHost		= "ro.plus1.wapstart.ru";
	private int age					= 0;
	private int applicationId		= 0;
	private String uid				= null;
	private RequestType requestType	= RequestType.html;
	private Gender gender			= Gender.Unknown;
	private String login			= null;
	private Set<BannerType> types	= null;
	private String preferredLocale	= null;
	private String displayMetrics	= null;
	private String displayOrientation = null;
	private String containerMetrics	= null;
	private Location location		= null;
	private String facebookUserHash	= null;
	private String twitterUserHash	= null;
	private String advertisingId	= null;
	private Boolean limitAdTrackingEnabled	= null;
	private String androidId		= null;
	private String buildSerial		= null;

	private boolean disabledOpenLinkAction = false;

	public static Plus1Request create() {
		return new Plus1Request();
	}

	public Plus1Request() {}

	public int getAge() {
		return age;
	}

	public Plus1Request setAge(int age) {
		this.age = age;

		return this;
	}

	public int getApplicationId() {
		return applicationId;
	}

	public Plus1Request setApplicationId(int applicationId) {
		this.applicationId = applicationId;

		return this;
	}

	public boolean hasUID() {
		return uid != null;
	}

	public String getUID() {
		return uid;
	}

	public Plus1Request setUid(String uid) {
		this.uid = uid;

		return this;
	}

	public String getPreferredLocale() {
		return preferredLocale;
	}

	public Plus1Request setPreferredLocale(String locale) {
		this.preferredLocale = locale;

		return this;
	}

	public String getDisplayMetrics() {
		return displayMetrics;
	}

	public Plus1Request setDisplayMetrics(String metrics) {
		this.displayMetrics = metrics;

		return this;
	}

	public String getDisplayOrientation() {
		return displayOrientation;
	}

	public Plus1Request setDisplayOrientation(String orientation) {
		this.displayOrientation = orientation;

		return this;
	}

	public String getContainerMetrics() {
		return containerMetrics;
	}

	public Plus1Request setContainerMetrics(String metrics) {
		this.containerMetrics = metrics;

		return this;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public Plus1Request setRequestType(RequestType requestType) {
		this.requestType = requestType;

		return this;
	}

	public Gender getGender() {
		return gender;
	}

	public Plus1Request setGender(Gender sex) {
		this.gender = sex;

		return this;
	}

	public String getLogin() {
		return login;
	}

	public Plus1Request setLogin(String login) {
		this.login = login;

		return this;
	}

	public Plus1Request addType(BannerType type) {
		if (types == null)
			this.types = new HashSet<BannerType>();

		if (!type.equals(BannerType.Undefined))
			types.add(type);

		return this;
	}

	public Plus1Request clearTypes() {
		if (types != null)
			types.clear();

		return this;
	}

	public String getServerHost() {
		return serverHost;
	}

	public Plus1Request setServerHost(String hostname) {
		this.serverHost = hostname;

		return this;
	}

	public Location getLocation() {
		return location;
	}

	public Plus1Request setLocation(Location location) {
		this.location = location;

		return this;
	}

	public String getAdvertisingId() {
		return advertisingId;
	}

	public Plus1Request setAdvertisingId(String advertisingId) {
		this.advertisingId = advertisingId;

		return this;
	}

	public String getFacebookUserHash() {
		return facebookUserHash;
	}

	public Plus1Request setFacebookUserHash(String facebookUserHash) {
		this.facebookUserHash = facebookUserHash;

		return this;
	}

	public String getTwitterUserHash() {
		return twitterUserHash;
	}

	public Plus1Request setTwitterUserHash(String twitterUserHash) {
		this.twitterUserHash = twitterUserHash;

		return this;
	}

	public boolean isLimitAdTrackingEnabled() {
		if (limitAdTrackingEnabled != null)
			return limitAdTrackingEnabled.booleanValue();

		return false;
	}

	public Plus1Request setLimitAdTrackingEnabled(Boolean limitAdTrackingEnabled) {
		this.limitAdTrackingEnabled = limitAdTrackingEnabled;

		return this;
	}

	public String getAndroidId() {
		return androidId;
	}

	public Plus1Request setAndroidId(String androidId) {
		this.androidId = androidId;

		return this;
	}

	public String getBuildSerial() {
		return buildSerial;
	}

	public Plus1Request setBuildSerial(String buildSerial) {
		this.buildSerial = buildSerial;

		return this;
	}

	public boolean isDisabledOpenLinkAction() {
		return disabledOpenLinkAction;
	}

	/**
	 * NOTE: This method disables interactions with default browser
	 *        on initial sdk requests. This may be helpful if you didn't
	 *        announced app scheme in manifest file or if you want to
	 *        neutralize possible negative effect of "jumping to the browser".
	 */
	public Plus1Request setDisabledOpenLinkAction(boolean orly) {
		this.disabledOpenLinkAction = orly;

		return this;
	}

	public String getUrl() {
		return getUrl(getRequestType());
	}

	public String getUrl(RequestType requestType) {
		Uri.Builder builder = new Uri.Builder();

		builder.scheme("http");
		builder.authority(getServerHost());
		builder.path(
			String.format(
				"v%d/%d.%s",
				REQUEST_VERSION,
				getApplicationId(),
				requestType.toString()
			)
		);

		if (hasUID())
			builder.appendQueryParameter("uid", getUID());

		if (isDisabledOpenLinkAction())
			builder.appendQueryParameter("disabledOpenLinkAction", "1");

		return builder.build().toString();
	}

	public UrlEncodedFormEntity getUrlEncodedFormEntity(RequestType requestType)
		throws UnsupportedEncodingException
	{
		List<NameValuePair> list = new ArrayList<NameValuePair>();

		addBaseParamsToList(list);

		if (requestType.equals(RequestType.init))
			addDeviceParamsToList(list);

		return new UrlEncodedFormEntity(list);
	}

	private void addBaseParamsToList(List<NameValuePair> list)
	{
		list.add(new BasicNameValuePair("platform", "Android"));
		list.add(new BasicNameValuePair("version", Build.VERSION.RELEASE));
		list.add(new BasicNameValuePair("sdkver", Constants.SDK_VERSION));

		if (types != null && !types.isEmpty()) {
			for (BannerType bannerType : types) {
				list.add(
					new BasicNameValuePair(
						"type[]",
						String.valueOf(bannerType.ordinal())
					)
				);
			}
		}

		if (getDisplayOrientation() != null) {
			list.add(
				new BasicNameValuePair(
					"display-orientation",
					getDisplayOrientation()
				)
			);
		}

		if (getContainerMetrics() != null) {
			list.add(
				new BasicNameValuePair(
					"container-metrics",
					getContainerMetrics()
				)
			);
		}

		if (getLocation() != null) {
			list.add(
				new BasicNameValuePair(
					"location",
					String.format(
						"%s;%s",
						getLocation().getLatitude(),
						getLocation().getLongitude()
					)
				)
			);
		}
	}

	private void addDeviceParamsToList(List<NameValuePair> list)
	{
		if (!getGender().equals(Gender.Unknown)) {
			list.add(
				new BasicNameValuePair(
					"sex",
					String.valueOf(getGender().ordinal())
				)
			);
		}

		if (getAge() != 0) {
			list.add(
				new BasicNameValuePair(
					"age",
					String.valueOf(getAge())
				)
			);
		}

		if (getLogin() != null) {
			try {
				list.add(
					new BasicNameValuePair(
						"login",
						URLEncoder.encode(getLogin(), HTTP.UTF_8)
					)
				);
			} catch (UnsupportedEncodingException e) {
				list.add(new BasicNameValuePair("login", getLogin()));
			}
		}

		if (getPreferredLocale() != null) {
			list.add(
				new BasicNameValuePair(
					"preferred-locale",
					getPreferredLocale()
				)
			);
		}

		if (getDisplayMetrics() != null) {
			list.add(
				new BasicNameValuePair(
					"display-metrics",
					getDisplayMetrics()
				)
			);
		}

		if (getAdvertisingId() != null) {
			list.add(
				new BasicNameValuePair(
					"google-advertising-id",
					getAdvertisingId()
				)
			);
		}

		if (getFacebookUserHash() != null) {
			list.add(
				new BasicNameValuePair(
					"facebook-user-id",
					getFacebookUserHash()
				)
			);
		}

		if (getTwitterUserHash() != null) {
			list.add(
				new BasicNameValuePair(
					"twitter-user-id",
					getTwitterUserHash()
				)
			);
		}

		if (getAndroidId() != null) {
			list.add(
				new BasicNameValuePair("android-id", getAndroidId())
			);
		}

		if (getBuildSerial() != null) {
			list.add(
				new BasicNameValuePair("android-build-serial", getBuildSerial())
			);
		}

		if (isLimitAdTrackingEnabled()) {
			list.add(
				new BasicNameValuePair("limit-ad-tracking-enabled", "1")
			);
		}
	}
}
