package views.avatar;

public class GroupAvatarChooserActivity extends AvatarChooserActivity {
    public boolean showFinalCropped() {
        return false;
    }

    ;

    @Override
    public void submit() {
        finishAndReturnResult();
    }

    @Override
    protected void useDefault() {
        finishAndReturnDefaultResult();
    }
}


