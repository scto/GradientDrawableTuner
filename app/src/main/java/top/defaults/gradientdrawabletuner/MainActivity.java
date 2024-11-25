package top.defaults.gradientdrawabletuner;

import android.content.Intent;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import java.util.List;

import top.defaults.gradientdrawabletuner.databinding.ActivityMainBinding;
import top.defaults.gradientdrawabletuner.db.AppDatabase;
import top.defaults.gradientdrawabletuner.db.DrawableSpec;
import top.defaults.gradientdrawabletuner.db.DrawableSpecFactory;

public class MainActivity extends AppCompatActivity {

    
     TextView statusTextView;
     ImageView imageView;
    RadioGroup shapeSwitcher;
    ValueRow cornerRadiusRow;
    Group fourCorners;

    private DrawableViewModel viewModel;
    private DrawableSpec currentDrawableSpec = DrawableSpecFactory.tempSpec();
    private MutableLiveData<Boolean> isEdited = new MutableLiveData<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.crafting_shape));

        viewModel = ViewModelProviders.of(this).get(DrawableViewModel.class);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        viewModel.apply(currentDrawableSpec.getProperties());
        
        binding.reviewCode.setOnClickListener(v->{
            Intent intent = new Intent(this, XmlCodeViewActivity.class);
        intent.putExtra(XmlCodeViewActivity.EXTRA_PROPERTIES, viewModel.getDrawableProperties().getValue());
        startActivity(intent);
        });
        
        Resources resources = getResources();
        binding.shape.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != R.id.rectangle) {
                binding.cornerRadiusRow.setExtensionsChecked(false);
            }
            viewModel.updateProperty("shapeId", checkedId);
        });
        binding.cornerRadiusRow.setOnExtensionsCheckedListener(checked -> binding.fourCorners.setVisibility(checked ? View.VISIBLE : View.GONE));

        viewModel.getDrawableProperties().observe(this, properties -> {
            if (properties != null) {
                int width = properties.width;
                int height = properties.height;
                // enlarge the width/height with the strokeWidth for RECTANGLE/OVAL
                if (properties.shape != GradientDrawable.RING) {
                    width = width + properties.strokeWidth;
                    height = height + properties.strokeWidth;
                }
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                binding.imageView.setLayoutParams(params);

                isEdited.setValue(currentDrawableSpec.getId() == 0
                        || !properties.equals(currentDrawableSpec.getProperties()));
            }
        });

        isEdited.observe(this, status -> updateStatus());

        final int maxWidth = (int) (resources.getDisplayMetrics().widthPixels / 1.5);
        final int maxHeight = (int) (resources.getDisplayMetrics().heightPixels / 2.5);
        binding.setMaxWidth(maxWidth);
        binding.setMaxHeight(maxHeight);
        binding.setViewModel(viewModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newSpec:
                currentDrawableSpec = DrawableSpecFactory.tempSpec();
                viewModel.apply(currentDrawableSpec.getProperties());
                isEdited.setValue(true);
                break;
            case R.id.set:
                LiveData<List<DrawableSpec>> liveData = AppDatabase.getInstance(this).drawableSpecDao().getAll();
                liveData.observe(this, new Observer<List<DrawableSpec>>() {
                    @Override
                    public void onChanged(@Nullable List<DrawableSpec> drawableSpecs) {
                        if (drawableSpecs != null && drawableSpecs.size() > 0) {
                            new DrawableSpecChooser(MainActivity.this, drawableSpecs).show(imageView, drawableSpec -> {
                                currentDrawableSpec = drawableSpec;
                                viewModel.apply(currentDrawableSpec.getProperties());
                                isEdited.setValue(false);
                            });
                        }
                        liveData.removeObserver(this);
                    }
                });
                break;
            case R.id.save:
                currentDrawableSpec.setProperties(viewModel.getDrawableProperties().getValue());
                if (currentDrawableSpec.getId() == 0) {
                    SetNameDialogFragment setNameDialogFragment = new SetNameDialogFragment();
                    setNameDialogFragment.setCallback(name -> AppDatabase.execute(() -> {
                        currentDrawableSpec.setName(name);
                        long id = AppDatabase.getInstance(this).drawableSpecDao().insert(currentDrawableSpec);
                        currentDrawableSpec = AppDatabase.getInstance(this).drawableSpecDao().findById(id);
                        isEdited.postValue(false);
                    }));
                    setNameDialogFragment.show(getFragmentManager(), "setName");
                } else {
                    AppDatabase.execute(() -> {
                        AppDatabase.getInstance(this).drawableSpecDao().update(currentDrawableSpec);
                        isEdited.postValue(false);
                    });
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateStatus() {
        if (isEdited.getValue() == null || isEdited.getValue()) {
            SpannableString status = new SpannableString(String.format("Spec: [%s] - [Unsaved]", currentDrawableSpec.getName()));
            int start = "Spec: []".length() + currentDrawableSpec.getName().length();
            status.setSpan(new ForegroundColorSpan(
                    ContextCompat.getColor(this, R.color.status_unsaved_color)),
                    start, status.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.statusTextView.setText(status);
        } else {
            statusTextView.setText(String.format("Spec: [%s]", currentDrawableSpec.getName()));
        }
    }
}
