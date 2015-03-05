package com.noprom.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.noprom.app.R;
import com.noprom.app.fragment.MainFragment;
import com.noprom.app.widget.navigationliveo.NavigationLiveoAdapter;
import com.noprom.app.widget.navigationliveo.NavigationLiveoList;
import com.noprom.app.widget.navigationliveo.NavigationLiveoListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements NavigationLiveoListener {

    private final String TAG = "MainActivity";

    // 主布局相关控件开始
    public TextView mUserName;
    public TextView mUserEmail;
    public ImageView mUserPhoto;
    public ImageView mUserBackground;

    private ListView mList;
    private Toolbar mToolbar;

    private View mHeader;

    private TextView mTitleFooter;
    private ImageView mIconFooter;

    private int mColorName = 0;
    private int mColorIcon = 0;
    private int mColorSeparator = 0;

    private int mColorDefault = 0;
    private int mColorSelected = 0;
    private int mCurrentPosition = 1;
    private int mNewSelector = 0;
    private boolean mRemoveAlpha = false;
    private boolean mRemoveSelector = false;

    private List<Integer> mListIcon;
    private List<Integer> mListHeader;
    private List<String> mListNameItem;
    private SparseIntArray mSparseCounter;

    private DrawerLayout mDrawerLayout;
    private FrameLayout mRelativeDrawer;
    private RelativeLayout mFooterDrawer;

    private NavigationLiveoAdapter mNavigationAdapter;
    private ActionBarDrawerToggleCompat mDrawerToggle;
    private NavigationLiveoListener mNavigationListener;

    public static final String CURRENT_POSITION = "CURRENT_POSITION";

    // 主布局相关控件结束

//    private AppContext appContext;// 全局Conntext
//
//    private DoubleClickExitHelper mDoubleClickExitHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_main);

//        mDoubleClickExitHelper = new DoubleClickExitHelper(this);

        // TODO handle broadcast receiver

//        appContext = (AppContext) getApplication();
        // 网络连接判断





        // 初始化主布局控件
        this.initDrawerLayout(savedInstanceState);

    }





    /**
     * 初始化主布局控件
     */
    private void initDrawerLayout(Bundle savedInstanceState){

        // 设置当前所在的选项
        if (savedInstanceState != null) {
            setCurrentPosition(savedInstanceState.getInt(CURRENT_POSITION));
        }

        // 左侧的List
        mList = (ListView) findViewById(R.id.list);
        mList.setOnItemClickListener(new DrawerItemClickListener());

        // 顶部的ToolBar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // drawerToggle和监听器
        mDrawerToggle = new ActionBarDrawerToggleCompat(this, mDrawerLayout, mToolbar);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // 底部title
        mTitleFooter = (TextView) this.findViewById(R.id.titleFooter);
        mIconFooter = (ImageView) this.findViewById(R.id.iconFooter);

        // 底部drawer
        mFooterDrawer = (RelativeLayout) this.findViewById(R.id.footerDrawer);
        mFooterDrawer.setOnClickListener(onClickFooterDrawer);

        mRelativeDrawer = (FrameLayout) this.findViewById(R.id.relativeDrawer);

        this.setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Resources.Theme theme = this.getTheme();
                TypedArray typedArray = theme.obtainStyledAttributes(new int[]{android.R.attr.colorPrimary});
                mDrawerLayout.setStatusBarBackground(typedArray.getResourceId(0, 0));
            } catch (Exception e) {
                e.getMessage();
            }

            this.setElevationToolBar(15);
        }

        if (mList != null) {
            mountListNavigation(savedInstanceState);
        }

        if (savedInstanceState == null) {
            mNavigationListener.onItemClickNavigation(mCurrentPosition, R.id.container);
        }

        setCheckedItemNavigation(mCurrentPosition, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, mCurrentPosition);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mDrawerToggle != null) {
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mRelativeDrawer);
        mNavigationListener.onPrepareOptionsMenuNavigation(menu, mCurrentPosition, drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    /**
     * ActionBarDrawer处理事件
     */
    private class ActionBarDrawerToggleCompat extends ActionBarDrawerToggle {

        public ActionBarDrawerToggleCompat(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar){
            super(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        }

        @Override
        public void onDrawerClosed(View view) {
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            supportInvalidateOptionsMenu();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    /**
     * 左侧Item点击事件
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int mPosition = (position - 1);

            if (position != 0) {
                mNavigationListener.onItemClickNavigation(mPosition, R.id.container);
                setCurrentPosition(mPosition);
                setCheckedItemNavigation(mPosition, true);
            }

            mDrawerLayout.closeDrawer(mRelativeDrawer);
        }
    }

    private View.OnClickListener onClickUserPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mNavigationListener.onClickUserPhotoNavigation(v);
            mDrawerLayout.closeDrawer(mRelativeDrawer);
        }
    };

    private View.OnClickListener onClickFooterDrawer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mNavigationListener.onClickFooterItemNavigation(v);
            mDrawerLayout.closeDrawer(mRelativeDrawer);
        }
    };

    private void mountListNavigation(Bundle savedInstanceState){
        createUserDefaultHeader();
        setUserInformation();
        onInt(savedInstanceState);
        setAdapterNavigation();
    }

    private void setAdapterNavigation(){

        if (mNavigationListener == null){
            throw new RuntimeException(getString(R.string.start_navigation_listener));
        }

        List<Integer> mListExtra = new ArrayList<>();
        mListExtra.add(0, mNewSelector);
        mListExtra.add(1, mColorDefault);
        mListExtra.add(2, mColorIcon);
        mListExtra.add(3, mColorName);
        mListExtra.add(4, mColorSeparator);

        mNavigationAdapter = new NavigationLiveoAdapter(this, NavigationLiveoList.getNavigationAdapter(mListNameItem, mListIcon,
                mListHeader, mSparseCounter, mColorSelected, mRemoveSelector, this), mRemoveAlpha, mListExtra);

        mList.setAdapter(mNavigationAdapter);
    }

    /**
     * 创建用户Header区块
     */
    private void createUserDefaultHeader() {
        mHeader = getLayoutInflater().inflate(R.layout.navigation_list_header, mList, false);

        mUserName = (TextView) mHeader.findViewById(R.id.userName);
        mUserEmail = (TextView) mHeader.findViewById(R.id.userEmail);

        mUserPhoto = (ImageView) mHeader.findViewById(R.id.userPhoto);
        mUserPhoto.setOnClickListener(onClickUserPhoto);

        mUserBackground = (ImageView) mHeader.findViewById(R.id.userBackground);
        mList.addHeaderView(mHeader);
    }

    /**
     * 用户信息设置
     */
    public void setUserInformation(){
        this.mUserName.setText("Rudson Lima");
        this.mUserEmail.setText("rudsonlive@gmail.com");
        this.mUserPhoto.setImageResource(R.drawable.ic_rudsonlive);
        this.mUserBackground.setImageResource(R.drawable.ic_user_background);
    }

    /**
     * 创建左侧的ListItems
     * @param savedInstanceState onCreate(Bundle savedInstanceState).
     */
    public void onInt(Bundle savedInstanceState) {

        // set listener {required}
        this.setNavigationListener(this);

        //First item of the position selected from the list
        this.setDefaultStartPositionNavigation(1);

        // name of the list items
        mListNameItem = new ArrayList<>();
        mListNameItem.add(0, getString(R.string.inbox));
        mListNameItem.add(1, getString(R.string.starred));
        mListNameItem.add(2, getString(R.string.sent_mail));
        mListNameItem.add(3, getString(R.string.drafts));
        mListNameItem.add(4, getString(R.string.more_markers)); //This item will be a subHeader
        mListNameItem.add(5, getString(R.string.trash));
        mListNameItem.add(6, getString(R.string.spam));

        // icons list items
        List<Integer> mListIconItem = new ArrayList<>();
        mListIconItem.add(0, R.drawable.ic_inbox_black_24dp);
        mListIconItem.add(1, R.drawable.ic_star_black_24dp); //Item no icon set 0
        mListIconItem.add(2, R.drawable.ic_send_black_24dp); //Item no icon set 0
        mListIconItem.add(3, R.drawable.ic_drafts_black_24dp);
        mListIconItem.add(4, 0); //When the item is a subHeader the value of the icon 0
        mListIconItem.add(5, R.drawable.ic_delete_black_24dp);
        mListIconItem.add(6, R.drawable.ic_report_black_24dp);

        //{optional} - Among the names there is some subheader, you must indicate it here
        List<Integer> mListHeaderItem = new ArrayList<>();
        mListHeaderItem.add(4);

        //{optional} - Among the names there is any item counter, you must indicate it (position) and the value here
        SparseIntArray mSparseCounterItem = new SparseIntArray(); //indicate all items that have a counter
        mSparseCounterItem.put(0, 7);
        mSparseCounterItem.put(1, 123);
        mSparseCounterItem.put(6, 250);

        //If not please use the FooterDrawer use the setFooterVisible(boolean visible) method with value false
        this.setFooterInformationDrawer(R.string.settings, R.drawable.ic_settings_black_24dp);

        this.setNavigationAdapter(mListNameItem, mListIconItem, mListHeaderItem, mSparseCounterItem);
    }


    /**
     * Set adapter attributes
     * @param listNameItem list name item.
     * @param listIcon list icon item.
     * @param listItensHeader list header name item.
     * @param sparceItensCount sparce count item.
     */
    public void setNavigationAdapter(List<String> listNameItem, List<Integer> listIcon, List<Integer> listItensHeader, SparseIntArray sparceItensCount){
        this.mListNameItem = listNameItem;
        this.mListIcon = listIcon;
        this.mListHeader = listItensHeader;
        this.mSparseCounter = sparceItensCount;
    }

    /**
     * Set adapter attributes
     * @param listNameItem list name item.
     * @param listIcon list icon item.
     */
    public void setNavigationAdapter(List<String> listNameItem, List<Integer> listIcon){
        this.mListNameItem = listNameItem;
        this.mListIcon = listIcon;
    }

    /**
     * Starting listener navigation
     * @param navigationListener listener.
     */
    public void setNavigationListener(NavigationLiveoListener navigationListener){
        this.mNavigationListener = navigationListener;
    };

    /**
     * First item of the position selected from the list
     * @param position ...
     */
    public void setDefaultStartPositionNavigation(int position){
        this.mCurrentPosition = position;
    }

    /**
     * 设置当前所在的选项
     * @param position
     */
    private void setCurrentPosition(int position){
        this.mCurrentPosition = position;
    }

    /**
     * get position in the last clicked item list
     */
    public int getCurrentPosition(){
        return this.mCurrentPosition;
    }



    /**
     * Select item clicked
     * @param position item position.
     * @param checked true to check.
     */
    public void setCheckedItemNavigation(int position, boolean checked){
        this.mNavigationAdapter.resetarCheck();
        this.mNavigationAdapter.setChecked(position, checked);
    }

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     */
    public void setFooterInformationDrawer(String title, int icon){

        if (title == null){
            throw new RuntimeException(getString(R.string.title_null_or_empty));
        }

        if (title.trim().equals("")){
            throw new RuntimeException(getString(R.string.title_null_or_empty));
        }

        mTitleFooter.setText(title);

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else{
            mIconFooter.setImageResource(icon);
        }
    }

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @param colorName item footer name color.
     * @param colorIcon item footer icon color.
     */
    public void setFooterInformationDrawer(String title, int icon, int colorName, int colorIcon){

        if (title == null){
            throw new RuntimeException(getString(R.string.title_null_or_empty));
        }

        if (title.trim().equals("")){
            throw new RuntimeException(getString(R.string.title_null_or_empty));
        }

        mTitleFooter.setText(title);

        if (colorName > 0){
            mTitleFooter.setTextColor(getResources().getColor(colorName));
        }

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else{
            mIconFooter.setImageResource(icon);

            if ( colorIcon > 0) {
                mIconFooter.setColorFilter(getResources().getColor(colorIcon));
            }
        }
    };

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     */
    public void setFooterInformationDrawer(int title, int icon){

        if (title == 0){
            throw new RuntimeException(getString(R.string.title_null_or_empty));
        }

        mTitleFooter.setText(getString(title));

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else{
            mIconFooter.setImageResource(icon);
        }
    };

    /**
     * Information footer list item
     * @param title item footer name.
     * @param icon item footer icon.
     * @param colorName item footer name color.
     * @param colorIcon item footer icon color.
     */
    public void setFooterInformationDrawer(int title, int icon, int colorName, int colorIcon){

        if (title == 0){
            throw new RuntimeException(getString(R.string.title_null_or_empty));
        }

        mTitleFooter.setText(title);

        if (colorName > 0){
            mTitleFooter.setTextColor(getResources().getColor(colorName));
        }

        if (icon == 0){
            mIconFooter.setVisibility(View.GONE);
        }else{
            mIconFooter.setImageResource(icon);

            if ( colorIcon > 0) {
                mIconFooter.setColorFilter(getResources().getColor(colorIcon));
            }
        }
    };

    /**
     * If not want to use the footer item just put false
     * @param visible true or false.
     */
    public void setFooterNavigationVisible(boolean visible){
        this.mFooterDrawer.setVisibility((visible) ? View.VISIBLE : View.GONE);
    }

    /**
     * Item color selected in the list - name and icon (use before the setNavigationAdapter)
     * @param colorId color id.
     */
    public void setColorSelectedItemNavigation(int colorId){
        this.mColorSelected = colorId;
    }

    /**
     * Footer icon color
     * @param colorId color id.
     */
    public void setFooterIconColorNavigation(int colorId){
        this.mIconFooter.setColorFilter(getResources().getColor(colorId));
    }

    /**
     * Item color default in the list - name and icon (use before the setNavigationAdapter)
     * @param colorId color id.
     */
    public void setColorDefaultItemNavigation(int colorId){
        this.mColorDefault = colorId;
    }

    /**
     * Icon item color in the list - icon (use before the setNavigationAdapter)
     * @param colorId color id.
     */
    public void setColorIconItemNavigation(int colorId){
        this.mColorIcon = colorId;
    }

    /**
     * Separator item subHeader color in the list - icon (use before the setNavigationAdapter)
     * @param colorId color id.
     */
    public void setColorSeparatorItemSubHeaderNavigation(int colorId){
        this.mColorSeparator = colorId;
    }

    /**
     * Name item color in the list - name (use before the setNavigationAdapter)
     * @param colorId color id.
     */
    public void setColorNameItemNavigation(int colorId){
        this.mColorName = colorId;
    }

    /**
     * New selector navigation
     * @param resourceSelector drawable xml - selector.
     */
    public void setNewSelectorNavigation(int resourceSelector){

        if (mRemoveSelector){
            throw new RuntimeException(getString(R.string.remove_selector_navigation));
        }

        this.mNewSelector = resourceSelector;
    }

    /**
     * Remove selector navigation
     */
    public void removeSelectorNavigation(){
        this.mRemoveSelector = true;
    }

    /**
     * New name item
     * @param position item position.
     * @param name new name
     */
    public void setNewName(int position, String name){
        this.mNavigationAdapter.setNewName(position, name);
    }

    /**
     * New name item
     * @param position item position.
     * @param name new name
     */
    public void setNewName(int position, int name){
        this.mNavigationAdapter.setNewName(position, getString(name));
    }

    /**
     * New name item
     * @param position item position.
     * @param icon new icon
     */
    public void setNewIcon(int position, int icon){
        this.mNavigationAdapter.setNewIcon(position, icon);
    }

    /**
     * New information item navigation
     * @param position item position.
     * @param name new name
     * @param icon new icon
     * @param counter new counter
     */
    public void setNewInformationItem(int position, int name, int icon, int counter){
        this.mNavigationAdapter.setNewInformationItem(position, getString(name), icon, counter);
    }

    /**
     * New information item navigation
     * @param position item position.
     * @param name new name
     * @param icon new icon
     * @param counter new counter
     */

    public void setNewInformationItem(int position, String name, int icon, int counter){
        this.mNavigationAdapter.setNewInformationItem(position, name, icon, counter);
    }

    /**
     * New counter value
     * @param position item position.
     * @param value new counter value.
     */
    public void setNewCounterValue(int position, int value){
        this.mNavigationAdapter.setNewCounterValue(position, value);
    }

    /**
     * Increasing counter value
     * @param position item position.
     * @param value new counter value (old value + new value).
     */
    public void setIncreasingCounterValue(int position, int value){
        this.mNavigationAdapter.setIncreasingCounterValue(position, value);
    }

    /**
     * Decrease counter value
     * @param position item position.
     * @param value new counter value (old value - new value).
     */
    public void setDecreaseCountervalue(int position, int value){
        this.mNavigationAdapter.setDecreaseCountervalue(position, value);
    }

    /**
     * Remove alpha item navigation (use before the setNavigationAdapter)
     */
    public void removeAlphaItemNavigation(){
        this.mRemoveAlpha = !mRemoveAlpha;
    }

    /**
     * public void setElevation (float elevation)
     * Added in API level 21
     * Default value is 15
     * @param elevation Sets the base elevation of this view, in pixels.
     */
    public void setElevationToolBar(float elevation){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getToolbar().setElevation(elevation);
        }
    }

    /**
     * Remove default Header
     */
    public void showDefaultHeader() {
        if (mHeader == null){
            throw new RuntimeException(getString(R.string.header_not_created));
        }

        mList.addHeaderView(mHeader);
    }

    /**
     * Remove default Header
     */
    private void removeDefaultHeader() {
        if (mHeader == null){
            throw new RuntimeException(getString(R.string.header_not_created));
        }

        mList.removeHeaderView(mHeader);
    }

    /**
     * Add custom Header
     * @param v ...
     */
    public void addCustomHeader(View v) {
        if (v == null){
            throw new RuntimeException(getString(R.string.custom_header_not_created));
        }

        removeDefaultHeader();
        mList.addHeaderView(v);
    }

    /**
     * Remove default Header
     * @param v ...
     */
    public void removeCustomdHeader(View v) {
        if (v == null){
            throw new RuntimeException(getString(R.string.custom_header_not_created));
        }

        mList.removeHeaderView(v);
    }

    /**
     * get listview
     */
    public ListView getListView() {
        return this.mList;
    }

    /**
     * get toolbar
     */
    public Toolbar getToolbar() {
        return this.mToolbar;
    }

    /**
     * Open drawer
     */
    public void openDrawer() {
        mDrawerLayout.openDrawer(mRelativeDrawer);
    }

    /**
     * Close drawer
     */
    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mRelativeDrawer);
    }

    @Override
    public void onBackPressed() {

        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mRelativeDrawer);
        if (drawerOpen) {
            mDrawerLayout.closeDrawer(mRelativeDrawer);
        } else {
            super.onBackPressed();
        }
    }

    /*以下为DrawerLayout Listener事件*/

    @Override
    public void onItemClickNavigation(int position, int layoutContainerId) {

        FragmentManager mFragmentManager = getSupportFragmentManager();

        Fragment mFragment = new MainFragment();

        if (mFragment != null){
            mFragmentManager.beginTransaction().replace(layoutContainerId, mFragment).commit();
        }
    }

    @Override
    public void onPrepareOptionsMenuNavigation(Menu menu, int position, boolean visible) {

        //hide the menu when the navigation is opens
        switch (position) {
            case 0:
                menu.findItem(R.id.menu_add).setVisible(!visible);
                menu.findItem(R.id.menu_search).setVisible(!visible);
                break;

            case 1:
                menu.findItem(R.id.menu_add).setVisible(!visible);
                menu.findItem(R.id.menu_search).setVisible(!visible);
                break;
        }
    }

    @Override
    public void onClickUserPhotoNavigation(View v) {
        //user photo onClick
        Toast.makeText(this, R.string.open_user_profile, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickFooterItemNavigation(View v) {
        //footer onClick
        startActivity(new Intent(this, SettingsActivity.class));
    }







}
