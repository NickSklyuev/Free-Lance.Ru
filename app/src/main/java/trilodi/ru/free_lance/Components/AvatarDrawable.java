package trilodi.ru.free_lance.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.Locale;

import trilodi.ru.free_lance.Config;
import trilodi.ru.free_lance.Models.FavoriteUser;
import trilodi.ru.free_lance.Models.User;
import trilodi.ru.free_lanceru.R;


public class AvatarDrawable extends Drawable {

    private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static TextPaint namePaint;
    private static TextPaint namePaintSmall;
    private static int[] arrColors = {0xffe56555, 0xfff28c48, 0xffeec764, 0xff76c84d, 0xff5fbed5, 0xff549cdd, 0xff8e85ee, 0xfff2749a};
    private static int[] arrColorsProfiles = {0xffd86f65, 0xfff69d61, 0xfffabb3c, 0xff67b35d, 0xff56a2bb, 0xff5c98cd, 0xff8c79d2, 0xfff37fa6};
    private static int[] arrColorsProfilesBack = {0xffca6056, 0xfff18944, 0xff7d6ac4, 0xff56a14c, 0xff4492ac, 0xff4c84b6, 0xff7d6ac4, 0xff4c84b6};
    private static int[] arrColorsProfilesText = {0xfff9cbc5, 0xfffdddc8, 0xffcdc4ed, 0xffc0edba, 0xffb8e2f0, 0xffb3d7f7, 0xffcdc4ed, 0xffb3d7f7};
    private static int[] arrColorsNames = {0xffca5650, 0xffd87b29, 0xff4e92cc, 0xff50b232, 0xff42b1a8, 0xff4e92cc, 0xff4e92cc, 0xff4e92cc};
    private static int[] arrColorsButtons = {R.drawable.bar_selector_red, R.drawable.bar_selector_orange, R.drawable.bar_selector_violet,
            R.drawable.bar_selector_green, R.drawable.bar_selector_cyan, R.drawable.bar_selector_blue, R.drawable.bar_selector_violet, R.drawable.bar_selector_blue};

    private static Drawable photoDrawable;

    private int color;
    private StaticLayout textLayout;
    private float textWidth;
    private float textHeight;
    private float textLeft;
    private boolean isProfile;
    private boolean drawBrodcast;
    private boolean drawPhoto;
    private boolean smallStyle;
    private static User user;

    public AvatarDrawable() {
        super();

        if (namePaint == null) {
            namePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            namePaint.setColor(0xffffffff);
            namePaint.setTextSize((int) Math.ceil(1 * 20));

            namePaintSmall = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            namePaintSmall.setColor(0xffffffff);
            namePaintSmall.setTextSize((int) Math.ceil(1 * 14));
        }
    }

    public AvatarDrawable(User user) {
        this(user, false);
    }

    public AvatarDrawable(FavoriteUser user) {
        this(user, false);
    }

    public AvatarDrawable(User user, boolean profile) {
        this();
        isProfile = profile;
        if (user != null) {
            this.user = user;
            setInfo(user.id, user.firstname, user.lastname, user.username, false);
        }
    }

    public AvatarDrawable(FavoriteUser user, boolean profile) {
        this();
        isProfile = profile;
        if (user != null) {
            this.user = (User) user;
            setInfo(user.id, user.firstname, user.lastname, user.username, false);
        }
    }


    public void setSmallStyle(boolean value) {
        smallStyle = value;
    }

    public static int getColorIndex(int id) {
        if (id >= 0 && id < 8) {
            return id;
        }
        try {
            String str;
            if (id >= 0) {
                str = String.format(Locale.US, "%d%d", id, Config.myUser.id);
            } else {
                str = String.format(Locale.US, "%d", id);
            }
            if (str.length() > 15) {
                str = str.substring(0, 15);
            }
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(str.getBytes());
            int b = digest[Math.abs(id % 16)];
            if (b < 0) {
                b += 256;
            }
            return Math.abs(b) % arrColors.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id % arrColors.length;
    }

    public static int getColorForId(int id) {
        return arrColors[getColorIndex(id)];
    }

    public static int getButtonColorForId(int id) {
        return arrColorsButtons[getColorIndex(id)];
    }

    public static int getProfileColorForId(int id) {
        return arrColorsProfiles[getColorIndex(id)];
    }

    public static int getProfileTextColorForId(int id) {
        return arrColorsProfilesText[getColorIndex(id)];
    }

    public static int getProfileBackColorForId(int id) {
        return arrColorsProfilesBack[getColorIndex(id)];
    }

    public static int getNameColorForId(int id) {
        return arrColorsNames[getColorIndex(id)];
    }

    public void setInfo(User user) {
        if (user != null) {
            setInfo(user.id, user.firstname, user.lastname, user.username, false);
        }
    }

    public void setColor(int value) {
        color = value;
    }

    public void setInfo(String id, String firstName, String lastName, String userName, boolean isBroadcast) {
        if (isProfile) {
            color = arrColorsProfiles[getColorIndex(Integer.parseInt(id))];
        } else {
            color = arrColors[getColorIndex(Integer.parseInt(id))];
        }

        drawBrodcast = isBroadcast;

        if (lastName == null || lastName.length() == 0) {
            lastName = userName;
        }

        if (firstName == null || firstName.length() == 0) {
            firstName = lastName;
            lastName = null;
        }

        String text = "";

        if (firstName != null && firstName.length() > 0) {
            text += firstName.substring(0, 1);
        }
        if (lastName != null && lastName.length() > 0) {
            String lastch = null;
            for (int a = lastName.length() - 1; a >= 0; a--) {
                if (lastch != null && lastName.charAt(a) == ' ') {
                    break;
                }
                lastch = lastName.substring(a, a + 1);
            }
            if (Build.VERSION.SDK_INT >= 16) {
                text += "\u200C" + lastch;
            } else {
                text += lastch;
            }
        } else if (firstName != null && firstName.length() > 0) {
            for (int a = firstName.length() - 1; a >= 0; a--) {
                if (firstName.charAt(a) == ' ') {
                    if (a != firstName.length() - 1 && firstName.charAt(a + 1) != ' ') {
                        if (Build.VERSION.SDK_INT >= 16) {
                            text += "\u200C" + firstName.substring(a + 1, a + 2);
                        } else {
                            text += firstName.substring(a + 1, a + 2);
                        }
                        break;
                    }
                }
            }
        }

        if (text.length() > 0) {
            text = text.toUpperCase();
            try {
                textLayout = new StaticLayout(text, (smallStyle ? namePaintSmall : namePaint), 100, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (textLayout.getLineCount() > 0) {
                    textLeft = textLayout.getLineLeft(0);
                    textWidth = textLayout.getLineWidth(0);
                    textHeight = textLayout.getLineBottom(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            textLayout = null;
        }
    }


    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds == null) {
            return;
        }
        int size = bounds.width();
        paint.setColor(color);
        canvas.save();
        canvas.translate(bounds.left, bounds.top);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);


            if (textLayout != null) {
                canvas.translate((size - textWidth) / 2 - textLeft, (size - textHeight) / 2);
                textLayout.draw(canvas);
            }

        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public int getIntrinsicWidth() {
        return 50;
    }

    @Override
    public int getIntrinsicHeight() {
        return 50;
    }
}