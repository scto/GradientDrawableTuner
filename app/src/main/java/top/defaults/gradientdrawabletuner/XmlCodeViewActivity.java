package top.defaults.gradientdrawabletuner;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import top.defaults.gradientdrawabletuner.code.CodeEditorColorSchemes;
import top.defaults.gradientdrawabletuner.code.CodeEditorLanguages;
import top.defaults.gradientdrawabletuner.databinding.ActivityXmlCodeViewBinding;
import top.defaults.gradientdrawabletuner.db.DrawablePropertiesInRoom;

public class XmlCodeViewActivity extends AppCompatActivity {

    static final String EXTRA_PROPERTIES = "extra_code";
    
    ActivityXmlCodeViewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.review_code);
        
        binding = ActivityXmlCodeViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        DrawablePropertiesInRoom properties = getIntent().getParcelableExtra(EXTRA_PROPERTIES);
        binding.editor.setText(ShapeXmlGenerator.shapeXmlString(properties).replaceAll("0x","#"));
        binding.editor.setEditable(false);
        loadColorScheme();
        Typeface typeface = Fonts.getDefault(this);
        if (typeface != null) {
            binding.editor.setTypefaceText(typeface);
        }
    }
    
    private void loadColorScheme() {
        binding.editor.setEditorLanguage(
            CodeEditorLanguages.loadTextMateLanguage(CodeEditorLanguages.SCOPE_NAME_XML));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Configuration configuration = getResources().getConfiguration();
            boolean isDarkTheme = configuration.isNightModeActive();
            if (isDarkTheme) {
                binding.editor.setColorScheme(
                        CodeEditorColorSchemes.loadTextMateColorScheme(
                                CodeEditorColorSchemes.THEME_DRACULA));
            } else {
                binding.editor.setColorScheme(
                        CodeEditorColorSchemes.loadTextMateColorScheme(
                                CodeEditorColorSchemes.THEME_GITHUB));
            }
        } else {
            binding.editor.setColorScheme(
                    CodeEditorColorSchemes.loadTextMateColorScheme(
                            CodeEditorColorSchemes.THEME_GITHUB));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
