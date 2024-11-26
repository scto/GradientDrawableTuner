package top.defaults.gradientdrawabletuner;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import top.defaults.gradientdrawabletuner.databinding.ActivityXmlCodeViewBinding;
import top.defaults.gradientdrawabletuner.db.DrawablePropertiesInRoom;

public class XmlCodeViewActivity extends AppCompatActivity {

    static final String EXTRA_PROPERTIES = "extra_code";

    TextView xmlCodeTextView;
    
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
        binding.xmlCodeTextView.setText(ShapeXmlGenerator.shapeXmlString(properties).replaceAll("0x","#"));
        Typeface typeface = Fonts.getDefault(this);
        if (typeface != null) {
            binding.xmlCodeTextView.setTypeface(typeface);
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
