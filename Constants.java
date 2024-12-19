import java.awt.*;

public class Constants {
    //file paths
    public static final String LOGIN_IMAGE_PATH = "resources/profile.png";
    //frame config
    public static final Dimension FRAME_SIZE = new Dimension(540,760);
    public static final Dimension TEXTFIELD_SIZE = new Dimension((int) (FRAME_SIZE.width * 0.80),50);
    public static final Dimension BUTTON_SIZE = TEXTFIELD_SIZE;
    public static final Dimension RESULT_DIALOG_SIZE = new Dimension((int)(FRAME_SIZE.width*0.33),(int)(FRAME_SIZE.height *0.165));

    //register config
    public static final Dimension REGISTER_LABEL_SIZE = new Dimension(FRAME_SIZE.width,150);

    //login config
    public static final Dimension LOGIN_IMAGE_SIZE = new Dimension(255,262);

}
