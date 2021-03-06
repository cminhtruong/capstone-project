package dalker.cmtruong.com.app.view.fragment;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import dalker.cmtruong.com.app.BuildConfig;
import dalker.cmtruong.com.app.R;
import dalker.cmtruong.com.app.adapter.DalkerReviewAdapter;
import dalker.cmtruong.com.app.database.AppExecutors;
import dalker.cmtruong.com.app.database.DalkerDatabase;
import dalker.cmtruong.com.app.helper.GlideApp;
import dalker.cmtruong.com.app.model.Review;
import dalker.cmtruong.com.app.model.User;
import dalker.cmtruong.com.app.viewmodel.AddFavoriteDalkerVMFactory;
import dalker.cmtruong.com.app.viewmodel.AddFavoriteDalkerViewModel;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

/**
 * @author davidetruong
 * @version 1.0
 * @since 03 August 2018
 */
public class FragmentDetailDalker extends Fragment {

    private static final String TAG = FragmentDetailDalker.class.getSimpleName();
    public static final String ACTION_UPDATED = "ACTION_UPDATE";
    View view;

    private static final String USER_LIST = "USER_LIST";
    private static final String USER_POSITION = "USER_POSITION";
    ArrayList<User> users;
    int position;

    @BindView(R.id.dalker_detail_photo)
    CircleImageView mDalkerPhoto;

    @BindView(R.id.dalker_detail_name_tv)
    TextView dalkerNameTv;

    @BindView(R.id.dalker_detail_address_tv)
    TextView dalkerAddressTv;

    @BindView(R.id.dalker_detail_tarif_tv)
    TextView dalkerPriceTv;

    @BindView(R.id.dalker_rating_bar)
    RatingBar dalkerRatingBar;

    @BindView(R.id.dalker_detail_text)
    TextView dalkerDescription;

    @BindView(R.id.dalker_review_list)
    RecyclerView reviewRV;

    ArrayList<Review> reviews;
    float rateAverage;

    @BindView(R.id.mToolbar)
    Toolbar mToolbar;

    @BindView(R.id.share_fab)
    FloatingActionButton fab;

    @BindView(R.id.comment_bt)
    Button comment_bt;

    @BindView(R.id.call_bt)
    Button call_bt;

    @BindView(R.id.insert_to_fav)
    ImageView insert_bt;

    @BindView(R.id.comment_error)
    TextView mError;

    @BindView(R.id.review_title)
    TextView textView;

    private DalkerReviewAdapter mAdapter;
    private DalkerDatabase mDB;

    private FirebaseStorage mStorage;
    private StorageReference mRef;

    private static final int DEFAULT_TASK_ID = -1;

    private int mUserId = DEFAULT_TASK_ID;

    public FragmentDetailDalker() {
    }

    public static FragmentDetailDalker getInstance(ArrayList<User> users, int position) {
        FragmentDetailDalker mFragment = new FragmentDetailDalker();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(USER_LIST, users);
        bundle.putInt(USER_POSITION, position);
        mFragment.setArguments(bundle);
        return mFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_dalker, container, false);
        ButterKnife.bind(this, view);
        setRetainInstance(true);
        mDB = DalkerDatabase.getsInstance(getActivity().getApplicationContext());
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (getArguments() != null) {
            users = getArguments().getParcelableArrayList(USER_LIST);
            position = getArguments().getInt(USER_POSITION);
        }
        mStorage = FirebaseStorage.getInstance();
        mRef = mStorage.getReference();
        initData(users.get(position));
        return view;
    }

    /**
     * Function to init the fragment
     *
     * @param user
     */
    private void initData(final User user) {
        if (user.getPictureURL() != null) {
            if (!user.getPictureURL().getLarge().equals("")) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(user.getPictureURL().getLarge());
                GlideApp.with(getContext())
                        .load(storageReference)
                        .error(R.mipmap.ic_launcher)
                        .into(mDalkerPhoto);
            } else {
                GlideApp.with(getContext())
                        .load(R.mipmap.ic_launcher_foreground)
                        .error(R.mipmap.ic_launcher)
                        .into(mDalkerPhoto);
            }

        } else {
            GlideApp.with(getContext())
                    .load(R.mipmap.ic_launcher_foreground)
                    .error(R.mipmap.ic_launcher)
                    .into(mDalkerPhoto);
        }

        dalkerNameTv.setText(String.format("%s %s", user.getName().getFirstName(), user.getName().getLastName()));
        dalkerAddressTv.setText(String.format("%s, %s", user.getLocation().getStreet(), user.getLocation().getCity()));
        dalkerPriceTv.setText(String.format("Price: %s", getString(R.string.dalker_price_test)));

        if (user.getDescription() == null)
            if (BuildConfig.FLAVOR.equals(getString(R.string.demo)))
                dalkerDescription.setText(getString(R.string.text_test));
            else
                dalkerDescription.setText(R.string.description_empty_error);
        else
            dalkerDescription.setText(user.getDescription());
        reviews = user.getReviews();
        rateAverage = getRateAverage(reviews);
        textView.setText(String.format("%s (%d)", getString(R.string.dalker_review), reviews.size()));
        if (user.getReviews() == null) {
            if (BuildConfig.FLAVOR.equals(getString(R.string.demo)))
                dalkerRatingBar.setRating(4.5f);
            else
                showError();
        } else {
            dalkerRatingBar.setRating(rateAverage);
            reviewRV.setLayoutManager(new LinearLayoutManager(getContext()));
            reviewRV.setHasFixedSize(true);
            mAdapter = new DalkerReviewAdapter(user.getReviews());
            reviewRV.setAdapter(mAdapter);
            setupReview();
        }

        setupButton(user);
        setupFavorite(user);
        Timber.d("Call the event here");
        insert_bt.setOnClickListener(v -> {
            AppExecutors.getInstance().diskIO().execute(() -> {
                if (mUserId == DEFAULT_TASK_ID) {
                    Timber.d("Something wrong here");
                    mDB.userDAO().insertUser(user);
                    String text = getString(R.string.add) + user.getName().getFirstName() + " " + user.getName().getLastName() + getString(R.string.success);
                    Snackbar.make(getActivity().findViewById(R.id.detail_dalker_container), text, Snackbar.LENGTH_LONG).show();
                    Timber.d("user:%s", user.toString());
                    insert_bt.setImageResource(R.drawable.ic_favorite_black_48dp);
                    Intent intent = new Intent(ACTION_UPDATED);
                    getActivity().getBaseContext().sendBroadcast(intent);
                } else {
                    Timber.d("user ID: %s", String.valueOf(mUserId));
                    insert_bt.setClickable(false);
                    insert_bt.setFocusable(false);
                }
            });
        });
    }

    private void setupReview() {
        reviewRV.setVisibility(View.VISIBLE);
        mError.setVisibility(View.GONE);
    }

    private void showError() {
        reviewRV.setVisibility(View.GONE);
        mError.setVisibility(View.VISIBLE);
    }

    /**
     * Function to calcul dalker average rate
     *
     * @param reviews
     * @return average|sum
     */
    private float getRateAverage(ArrayList<Review> reviews) {
        int sum = 0;
        float average;
        if (reviews != null && reviews.size() > 0) {
            for (Review review : reviews)
                sum += review.getRate();
            average = sum / reviews.size();
            return average;
        }
        return sum;
    }

    /**
     * function to setup the FAB button
     *
     * @param user
     */
    private void setupButton(final User user) {
        fab.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType(getString(R.string.type_text));
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            share.putExtra(Intent.EXTRA_TEXT, user.getName().getFirstName() + " " + user.getName().getLastName());
            startActivity(Intent.createChooser(share, getString(R.string.share)));
        });

        call_bt.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(getString(R.string.tel), user.getPhone(), null));
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
            startActivity(intent, bundle);
        });

        comment_bt.setOnClickListener(v -> {
            CommentFragment commentFragment = CommentFragment.getInstance(user);
            commentFragment.show(getActivity().getSupportFragmentManager(), TAG);

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d(String.valueOf(item.getItemId()));
        return super.onOptionsItemSelected(item);
    }

    private void setupFavorite(User user) {
        AddFavoriteDalkerVMFactory factory = new AddFavoriteDalkerVMFactory(mDB, user.getIdUser());
        final AddFavoriteDalkerViewModel viewModel = ViewModelProviders.of(this, factory).get(AddFavoriteDalkerViewModel.class);
        viewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                viewModel.getUser().removeObserver(this);
                if (user == null)
                    insert_bt.setImageResource(R.drawable.ic_favorite_border_blue_48dp);
                else
                    insert_bt.setImageResource(R.drawable.ic_favorite_black_48dp);
                Timber.d("check 1");
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
