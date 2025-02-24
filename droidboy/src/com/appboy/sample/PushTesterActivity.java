package com.appboy.sample;

import android.app.Notification;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.appboy.Constants;
import com.appboy.configuration.XmlAppConfigurationProvider;
import com.appboy.push.AppboyNotificationUtils;
import com.appboy.sample.util.SpinnerUtils;
import com.appboy.ui.support.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class PushTesterActivity extends AppboyFragmentActivity implements AdapterView.OnItemSelectedListener {
  XmlAppConfigurationProvider mAppConfigurationProvider;
  NotificationManagerCompat mNotificationManager;
  private static final String[] mWearExtraPageTitles = new String[]{"Extra page (title)", "So Many Pages! (title)", "Last Page (title)"};
  private static final String[] mWearExtraPageContents = new String[]{"Space (content)", "The Final Frontier (content)", "There's a lot of text here. There's so much text here! So many lines! This is also the last extra page (content)"};
  private final String mTitle = "Welcome to Appboy (title)!";
  private final String mContent = "We hope you're enjoying our product (content).";
  private final String mBigTitle = "Appmazing (big title).";
  private boolean mUseBigSummary = false;
  private final String mBigSummary = "The Big Picture (big summary)";
  private final String mSummary = "This is what it's all about (summary)";
  private String mPriority = String.valueOf(Notification.PRIORITY_DEFAULT);
  private String mImage;
  private String mClickActionUrl;
  private String mCategory;
  private String mVisibility;
  private String mWearBackgroundImageUrl;
  private String mActionType;

  private boolean mUseSummary = false;
  private boolean mUseImage = false;
  private boolean mShouldOverflowText = false;
  private boolean mUseBigTitle = false;
  private boolean mUseClickAction = false;
  private boolean mUseCategory = false;
  private boolean mUseVisibility = false;
  private boolean mSetPublicVersion = false;
  private boolean mWearHideAppIcon = false;
  private boolean mWearUseBackgroundImage = false;
  private boolean mWearAddExtraPages = false;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.push_tester);
    setTitle("Push");
    mNotificationManager = NotificationManagerCompat.from(this);

    ((CheckBox) findViewById(R.id.push_tester_big_title)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mUseBigTitle = isChecked;
      }
    });
    ((CheckBox) findViewById(R.id.push_tester_summary)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mUseSummary = isChecked;
      }
    });
    ((CheckBox) findViewById(R.id.push_tester_big_summary)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mUseBigSummary = isChecked;
      }
    });
    ((CheckBox) findViewById(R.id.push_tester_overflow_text)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mShouldOverflowText = isChecked;
      }
    });
    ((CheckBox) findViewById(R.id.push_tester_set_public_version)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mSetPublicVersion = isChecked;
      }
    });
    ((CheckBox) findViewById(R.id.push_tester_wear_hide_app_icon)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mWearHideAppIcon = isChecked;
      }
    });
    ((CheckBox) findViewById(R.id.push_tester_wear_add_extra_pages)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mWearAddExtraPages = isChecked;
      }
    });

    // Creates the push image spinner.
    SpinnerUtils.setUpSpinner((Spinner) findViewById(R.id.push_image_spinner), this, R.array.push_image_options);

    // Creates the push priority spinner.
    SpinnerUtils.setUpSpinner((Spinner) findViewById(R.id.push_priority_spinner), this, R.array.push_priority_options);

    // Creates the push click action spinner.
    SpinnerUtils.setUpSpinner((Spinner) findViewById(R.id.push_click_action_spinner), this, R.array.push_click_action_options);

    // Creates the notification category spinner.
    SpinnerUtils.setUpSpinner((Spinner) findViewById(R.id.push_category_spinner), this, R.array.push_category_options);

    // Creates the visibility spinner.
    SpinnerUtils.setUpSpinner((Spinner) findViewById(R.id.push_visibility_spinner), this, R.array.push_visibility_options);

    // Creates the Wear background image spinner.
    SpinnerUtils.setUpSpinner((Spinner) findViewById(R.id.push_wear_background_image_spinner), this, R.array.push_wear_background_image_options);

    // Creates the push image spinner.
    SpinnerUtils.setUpSpinner((Spinner) findViewById(R.id.push_image_spinner), this, R.array.push_image_options);

    // Creates the push action spinner.
    SpinnerUtils.setUpSpinner((Spinner) findViewById(R.id.push_action_spinner), this, R.array.push_action_options);

    mAppConfigurationProvider = new XmlAppConfigurationProvider(this);
    Button pushTestButton = (Button) findViewById(R.id.test_push_button);
    pushTestButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        (new Thread(new Runnable() {
          public void run() {
            Bundle notificationExtras = new Bundle();
            notificationExtras.putString(Constants.APPBOY_PUSH_TITLE_KEY, generateDisplayValue(mTitle, mShouldOverflowText));
            notificationExtras.putString(Constants.APPBOY_PUSH_CONTENT_KEY, generateDisplayValue(mContent, mShouldOverflowText));
            notificationExtras.putString(Constants.APPBOY_PUSH_WEAR_HIDE_APP_ICON_KEY, String.valueOf(mWearHideAppIcon));

            int notificationId = AppboyNotificationUtils.getNotificationId(notificationExtras);
            notificationExtras.putInt(Constants.APPBOY_PUSH_NOTIFICATION_ID, notificationId);
            notificationExtras = addActionButtons(notificationExtras);

            if (mUseSummary) {
              notificationExtras.putString(Constants.APPBOY_PUSH_SUMMARY_TEXT_KEY, generateDisplayValue(mSummary, mShouldOverflowText));
            }
            if (mUseClickAction) {
              notificationExtras.putString(Constants.APPBOY_PUSH_DEEP_LINK_KEY, mClickActionUrl);
            }
            notificationExtras.putString(Constants.APPBOY_PUSH_PRIORITY_KEY, mPriority);
            if (mUseBigTitle) {
              notificationExtras.putString(Constants.APPBOY_PUSH_BIG_TITLE_TEXT_KEY, mBigTitle);
            }
            if (mUseBigSummary) {
              notificationExtras.putString(Constants.APPBOY_PUSH_BIG_SUMMARY_TEXT_KEY, mBigSummary);
            }
            if (mUseCategory) {
              notificationExtras.putString(Constants.APPBOY_PUSH_CATEGORY_KEY, mCategory);
            }
            if (mUseVisibility) {
              notificationExtras.putString(Constants.APPBOY_PUSH_VISIBILITY_KEY, mVisibility);
            }
            if (mSetPublicVersion) {
              try {
                notificationExtras.putString(Constants.APPBOY_PUSH_PUBLIC_NOTIFICATION_KEY, getPublicVersionNotificationString());
              } catch (JSONException jsonException) {
                Log.e(TAG, "Failed to created public version notification JSON string", jsonException);
              }
            }
            if (mWearUseBackgroundImage) {
              notificationExtras.putString(Constants.APPBOY_PUSH_WEAR_BACKGROUND_IMAGE_URL_KEY, mWearBackgroundImageUrl);
            }
            if (mWearAddExtraPages) {
              addWearExtraPagesToNotificationBuilder(notificationExtras);
            }

            // Manually build the appboy extras bundle. 
            Bundle appboyExtras = new Bundle();
            if (mUseImage) {
              if (Constants.IS_AMAZON) {
                // Amazon flattens the extras bundle so we have to put it in the regular notification
                // extras to imitate that functionality.
                notificationExtras.putString(Constants.APPBOY_PUSH_BIG_IMAGE_URL_KEY, mImage.replaceAll("&amp;", "&"));
                appboyExtras = new Bundle(notificationExtras);
              } else {
                appboyExtras.putString(Constants.APPBOY_PUSH_BIG_IMAGE_URL_KEY, mImage.replaceAll("&amp;", "&"));
              }
            }

            notificationExtras.putBundle(Constants.APPBOY_PUSH_EXTRAS_KEY, appboyExtras);
            Notification notification = AppboyNotificationUtils.getActiveNotificationFactory().createNotification(
              mAppConfigurationProvider,
              getApplicationContext(),
              notificationExtras,
              appboyExtras);
            mNotificationManager.notify(Constants.APPBOY_PUSH_NOTIFICATION_TAG, AppboyNotificationUtils.getNotificationId(notificationExtras), notification);
          }
        })).start();
      }
    });
  }

  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    switch (parent.getId()) {
      case R.id.push_image_spinner:
        String pushImageUriString = getResources().getStringArray(R.array.push_image_values)[parent.getSelectedItemPosition()];
        if (pushImageUriString != null && pushImageUriString.length() > 0) {
          mUseImage = true;
          mImage = pushImageUriString;
        } else {
          mUseImage = false;
        }
        break;
      case R.id.push_wear_background_image_spinner:
        String pushWearBackgroundImageUriString = getResources().getStringArray(R.array.push_wear_background_image_values)[parent.getSelectedItemPosition()];
        if (pushWearBackgroundImageUriString != null && pushWearBackgroundImageUriString.length() > 0) {
          mWearUseBackgroundImage = true;
          mWearBackgroundImageUrl = pushWearBackgroundImageUriString;
        } else {
          mWearUseBackgroundImage = false;
        }
        break;
      case R.id.push_priority_spinner:
        mPriority = getResources().getStringArray(R.array.push_priority_values)[parent.getSelectedItemPosition()];
        break;
      case R.id.push_click_action_spinner:
        String pushClickActionUriString = getResources().getStringArray(R.array.push_click_action_values)[parent.getSelectedItemPosition()];
        if (pushClickActionUriString != null && pushClickActionUriString.length() > 0) {
          mUseClickAction = true;
          mClickActionUrl = pushClickActionUriString;
        } else {
          mUseClickAction = false;
        }
        break;
      case R.id.push_category_spinner:
        mCategory = getResources().getStringArray(R.array.push_category_values)[parent.getSelectedItemPosition()];
        if (mCategory != null && mCategory.length() > 0) {
          mUseCategory = true;
        } else {
          mUseCategory = false;
        }
        break;
      case R.id.push_visibility_spinner:
        mVisibility = getResources().getStringArray(R.array.push_visibility_values)[parent.getSelectedItemPosition()];
        if (mVisibility != null && mVisibility.length() > 0) {
          mUseVisibility = true;
        } else {
          mUseVisibility = false;
        }
        break;
      case R.id.push_action_spinner:
        mActionType = getResources().getStringArray(R.array.push_action_values)[parent.getSelectedItemPosition()];
        break;
      default:
        Log.e(TAG, "Item selected for unknown spinner");
    }
  }

  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing
  }

  private String getPublicVersionNotificationString() throws JSONException {
    JSONObject publicVersionJSON = new JSONObject();
    publicVersionJSON.put(Constants.APPBOY_PUSH_TITLE_KEY, "Don't open in public (title)");
    publicVersionJSON.put(Constants.APPBOY_PUSH_CONTENT_KEY, "Please (content)");
    publicVersionJSON.put(Constants.APPBOY_PUSH_SUMMARY_TEXT_KEY, "Summary");
    return publicVersionJSON.toString();
  }

  private Bundle addActionButtons(Bundle notificationExtras) {
    if (StringUtils.isNullOrBlank(mActionType)) {
      return notificationExtras;
    }
    if (mActionType.equals(Constants.APPBOY_PUSH_ACTION_TYPE_OPEN)) {
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TYPE_KEY_TEMPLATE.replace("*", "0"), Constants.APPBOY_PUSH_ACTION_TYPE_OPEN);
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TEXT_KEY_TEMPLATE.replace("*", "0"), "Open app");
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_ICON_KEY_TEMPLATE.replace("*", "0"), getString(R.string.exit_to_app_icon_name));
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TYPE_KEY_TEMPLATE.replace("*", "1"), Constants.APPBOY_PUSH_ACTION_TYPE_NONE);
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TEXT_KEY_TEMPLATE.replace("*", "1"), "Close");
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_ICON_KEY_TEMPLATE.replace("*", "1"), getString(R.string.close_icon_name));
    } else if (mActionType.equals(Constants.APPBOY_PUSH_ACTION_TYPE_URI)) {
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TYPE_KEY_TEMPLATE.replace("*", "0"), Constants.APPBOY_PUSH_ACTION_TYPE_URI);
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TEXT_KEY_TEMPLATE.replace("*", "0"), "Appboy");
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_URI_KEY_TEMPLATE.replace("*", "0"), getString(R.string.appboy_url));
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_ICON_KEY_TEMPLATE.replace("*", "0"), getString(R.string.droidboy_icon_name));
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TYPE_KEY_TEMPLATE.replace("*", "1"), Constants.APPBOY_PUSH_ACTION_TYPE_URI);
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TEXT_KEY_TEMPLATE.replace("*", "1"), "Google");
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_URI_KEY_TEMPLATE.replace("*", "1"), getString(R.string.google_url));
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_ICON_KEY_TEMPLATE.replace("*", "1"), getString(R.string.world_icon_name));
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TYPE_KEY_TEMPLATE.replace("*", "2"), Constants.APPBOY_PUSH_ACTION_TYPE_NONE);
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TEXT_KEY_TEMPLATE.replace("*", "2"), "Close");
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_ICON_KEY_TEMPLATE.replace("*", "2"), getString(R.string.close_icon_name));
    } else if (mActionType.equals("deep_link")) {
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TYPE_KEY_TEMPLATE.replace("*", "0"), Constants.APPBOY_PUSH_ACTION_TYPE_URI);
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TEXT_KEY_TEMPLATE.replace("*", "0"), "Preferences");
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_URI_KEY_TEMPLATE.replace("*", "0"), getString(R.string.droidboy_deep_link));
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_ICON_KEY_TEMPLATE.replace("*", "0"), getString(R.string.settings_icon_name));
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TYPE_KEY_TEMPLATE.replace("*", "1"), Constants.APPBOY_PUSH_ACTION_TYPE_URI);
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TEXT_KEY_TEMPLATE.replace("*", "1"), "Telephone");
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_URI_KEY_TEMPLATE.replace("*", "1"), getString(R.string.telephone_uri));
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_ICON_KEY_TEMPLATE.replace("*", "1"), getString(R.string.heart_icon_name));
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TYPE_KEY_TEMPLATE.replace("*", "2"), Constants.APPBOY_PUSH_ACTION_TYPE_NONE);
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TEXT_KEY_TEMPLATE.replace("*", "2"), "Close");
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_ICON_KEY_TEMPLATE.replace("*", "2"), getString(R.string.close_icon_name));
    } else if (mActionType.equals(Constants.APPBOY_PUSH_ACTION_TYPE_SHARE)) {
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TYPE_KEY_TEMPLATE.replace("*", "0"), Constants.APPBOY_PUSH_ACTION_TYPE_SHARE);
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TEXT_KEY_TEMPLATE.replace("*", "0"), "Share");
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_ICON_KEY_TEMPLATE.replace("*", "0"), getString(R.string.share_icon_name));
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TYPE_KEY_TEMPLATE.replace("*", "1"), Constants.APPBOY_PUSH_ACTION_TYPE_NONE);
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TEXT_KEY_TEMPLATE.replace("*", "1"), "Close");
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_ICON_KEY_TEMPLATE.replace("*", "1"), getString(R.string.close_icon_name_1));
    } else if (mActionType.equals(getString(R.string.droidboy_custom_action))) {
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TYPE_KEY_TEMPLATE.replace("*", "0"), getString(R.string.droidboy_custom_action));
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TEXT_KEY_TEMPLATE.replace("*", "0"), "Droidboy!");
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_ICON_KEY_TEMPLATE.replace("*", "0"), getString(R.string.droidboy_icon_name));
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TYPE_KEY_TEMPLATE.replace("*", "1"), Constants.APPBOY_PUSH_ACTION_TYPE_NONE);
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_TEXT_KEY_TEMPLATE.replace("*", "1"), "Close");
      notificationExtras.putString(Constants.APPBOY_PUSH_ACTION_ICON_KEY_TEMPLATE.replace("*", "1"), getString(R.string.close_icon_name_2));
    }
    return notificationExtras;
  }

  // If shouldOverflowText is specified we concatenate an append string 5 times
  // This is to test big text and ellipsis cutoff in varying screen sizes
  private String generateDisplayValue(String field, boolean shouldOverFlowText) {
    String appendString = " 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20";
    if (shouldOverFlowText) {
      String returnText = field;
      for (int i = 0; i < 5; i++) {
        returnText = returnText + appendString;
      }
      return returnText;
    } else {
      return field;
    }
  }

  private void addWearExtraPagesToNotificationBuilder(Bundle notificationExtras) {
    for (int i = 0; i < mWearExtraPageTitles.length; i++) {
      String titleKey = Constants.APPBOY_PUSH_WEAR_EXTRA_PAGE_TITLE_KEY_PREFIX + i;
      notificationExtras.putString(titleKey, mWearExtraPageTitles[i]);
    }
    for (int i = 0; i < mWearExtraPageContents.length; i++) {
      String contentKey = Constants.APPBOY_PUSH_WEAR_EXTRA_PAGE_CONTENT_KEY_PREFIX + i;
      notificationExtras.putString(contentKey, mWearExtraPageContents[i]);
    }
  }
}
