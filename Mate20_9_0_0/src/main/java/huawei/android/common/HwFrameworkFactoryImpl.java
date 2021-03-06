package huawei.android.common;

import android.app.Activity;
import android.app.HwActivitySplitterImpl;
import android.app.HwKeyguardManager;
import android.app.HwWallpaperManager;
import android.app.IHwWallpaperInfoStub;
import android.app.IWallpaperManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.app.admin.IHwDeviceAdminInfo;
import android.aps.HwApsManager;
import android.aps.IApsManager;
import android.common.HwAnimationManager;
import android.common.HwDrmManager;
import android.common.HwFlogManager;
import android.common.HwFrameworkFactory.Factory;
import android.common.HwFrameworkFactory.IHwFeatureLoader;
import android.common.HwFrameworkFactory.IHwNetworkPolicyManager;
import android.common.HwFrameworkFactory.IHwSystemManager;
import android.common.HwFrameworkFactory.IHwWallpaperManager;
import android.common.HwFrameworkMonitor;
import android.common.HwMediaScannerManager;
import android.common.HwPackageManager;
import android.common.HwSettingsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.HwPackageParser;
import android.content.res.AssetManager;
import android.content.res.ConfigurationEx;
import android.content.res.HwPCResourcesUtils;
import android.content.res.IHwConfiguration;
import android.content.res.IHwPCResourcesUtils;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.cover.CoverManager;
import android.cover.IHwCoverManager;
import android.encrypt.HwPasswordUtil;
import android.encrypt.PasswordUtil;
import android.graphics.AwareBitmapCacher;
import android.graphics.IAwareBitmapCacher;
import android.hdm.HwDeviceManager.IHwDeviceManager;
import android.hsm.HwSystemManager.HsmInterface;
import android.hwgallerycache.IHwGalleryCacheManagerFactory;
import android.hwtheme.IHwThemeManagerFactory;
import android.inputmethodservice.HwInputMethodService;
import android.location.HwInnerLocationManagerImpl;
import android.location.IHwInnerLocationManager;
import android.media.HwAudioRecordImpl;
import android.media.HwMediaMonitorImpl;
import android.media.HwMediaRecorderImpl;
import android.media.HwMediaScannerImpl;
import android.media.IHwAudioRecord;
import android.media.IHwMediaMonitor;
import android.media.IHwMediaRecorder;
import android.media.hwmnote.HwMnoteInterfaceImpl;
import android.media.hwmnote.IHwMnoteInterface;
import android.mtp.HwMtpDatabaseImpl;
import android.mtp.HwMtpDatabaseManager;
import android.net.HwInnerConnectivityManager;
import android.net.HwInnerConnectivityManagerImpl;
import android.net.HwNetworkPolicyManager;
import android.net.INetworkPolicyManager;
import android.net.NetworkPolicyManager;
import android.net.booster.HwCommBoosterServiceManager;
import android.net.booster.IHwCommBoosterServiceManager;
import android.net.wifi.HwInnerNetworkManager;
import android.net.wifi.HwInnerNetworkManagerImpl;
import android.net.wifi.HwInnerWifiManager;
import android.net.wifi.HwInnerWifiManagerImpl;
import android.net.wifi.p2p.HwInnerWifiP2pManager;
import android.net.wifi.p2p.HwInnerWifiP2pManagerImpl;
import android.os.FreezeScreenApplicationMonitor;
import android.os.Handler;
import android.os.HwHandlerImpl;
import android.os.IFreezeScreenApplicationMonitor;
import android.os.IHwHandler;
import android.pc.HwPCManager;
import android.pc.HwPCManagerImpl;
import android.perf.HwOptPackageParser;
import android.perf.HwOptPackageParserImpl;
import android.provider.IHwMediaStore;
import android.rms.HwAppInnerBoostImpl;
import android.rms.HwSysResImpl;
import android.rms.HwSysResource;
import android.telephony.HwInnerTelephonyManager;
import android.telephony.HwInnerTelephonyManagerImpl;
import android.util.AttributeSet;
import android.util.HwLogExceptionInner;
import android.util.Log;
import android.util.LogException;
import android.view.HwAdCleaner;
import android.view.HwApsImpl;
import android.view.HwViewImpl;
import android.view.HwViewRootImpl;
import android.view.IHwView;
import android.view.animation.Interpolator;
import android.view.autofill.IHwAutofillHelper;
import android.vrsystem.IVRSystemServiceManager;
import android.vrsystem.VRSystemServiceManager;
import android.widget.FrameLayout;
import android.widget.HwWidgetManager;
import android.zrhung.IAppEyeUiProbe;
import android.zrhung.IZrHung;
import android.zrhung.ZrHungImpl;
import android.zrhung.appeye.AppEyeUiProbe;
import com.android.ims.HwImsManagerInner;
import com.android.internal.app.HwLocalePickerManager;
import com.android.internal.os.IHwPowerProfileManager;
import com.android.internal.telephony.HwBaseInnerSmsManager;
import com.android.internal.telephony.HwBaseInnerSmsManagerImpl;
import com.android.internal.view.IInputMethodManager;
import com.huawei.android.hardware.display.HwDisplayManagerGlobalEx;
import com.huawei.android.hardware.display.IHwDisplayManagerGlobalEx;
import com.huawei.android.hardware.display.IHwDisplayManagerGlobalInner;
import com.huawei.forcerotation.HwForceRotationManager;
import com.huawei.forcerotation.IForceRotationManager;
import com.huawei.hsm.HsmInterfaceImpl;
import com.huawei.hwanimation.AnimUtil;
import com.huawei.hwperformance.HwPerformance;
import com.huawei.hwperformance.HwPerformanceImpl;
import com.huawei.indexsearch.IIndexClearManager;
import com.huawei.indexsearch.IIndexSearchManager;
import com.huawei.indexsearch.IIndexSearchParser;
import com.huawei.indexsearch.IndexClearManager;
import com.huawei.indexsearch.IndexSearchManager;
import com.huawei.indexsearch.IndexSearchParser;
import huawei.android.app.HwActivityThreadImpl;
import huawei.android.app.HwApiCacheMangerEx;
import huawei.android.app.HwApplicationPackageManager;
import huawei.android.app.HwKeyguardManagerImpl;
import huawei.android.app.HwWallpaperInfoStubImpl;
import huawei.android.app.admin.HwDeviceAdminInfo;
import huawei.android.app.admin.HwDeviceManagerImpl;
import huawei.android.hwanimation.HwAnimationManagerImpl;
import huawei.android.hwdrm.HwDrmManagerImpl;
import huawei.android.hwgallerycache.HwGalleryCacheManagerFactoryImpl;
import huawei.android.hwnotification.HwNotificationResourceImpl;
import huawei.android.hwtheme.HwThemeManagerFactoryImpl;
import huawei.android.provider.HwMediaStoreImpl;
import huawei.android.provider.HwSettingsManagerImpl;
import huawei.android.security.HwBehaviorCollectManagerImpl;
import huawei.android.security.IHwBehaviorCollectManager;
import huawei.android.utils.HwFlogManagerImpl;
import huawei.android.view.autofill.HwAutofillHelper;
import huawei.android.view.inputmethod.HwSecImmHelper;
import huawei.android.widget.HwWidgetManagerImpl;
import huawei.com.android.internal.app.HwLocalePickerManagerImpl;
import huawei.com.android.internal.os.HwFLClassLoader;
import huawei.com.android.internal.os.HwFeatureLoader;
import huawei.com.android.internal.os.HwPowerProfileManagerImpl;
import huawei.com.android.internal.policy.PressGestureDetector;
import huawei.cust.HwCarrierConfigPolicy;
import huawei.cust.HwGetCfgFile;
import huawei.cust.IHwCarrierConfigPolicy;
import huawei.cust.IHwGetCfgFileConfig;

public class HwFrameworkFactoryImpl implements Factory {
    public static final String ACTION_HW_CHOOSER = "com.huawei.intent.action.hwCHOOSER";
    private static final String TAG = "HwFrameworkFactoryImpl";

    public static class HwNetworkPolicyManagerImpl implements IHwNetworkPolicyManager {
        public NetworkPolicyManager getInstance(Context ctx, INetworkPolicyManager service) {
            return new HwNetworkPolicyManager(ctx, service);
        }
    }

    public static class HwSystemManagerImpl implements IHwSystemManager {
        public HsmInterface getHsmInstance() {
            return new HsmInterfaceImpl();
        }
    }

    public static class HwWallpaperManagerImpl implements IHwWallpaperManager {
        public WallpaperManager getInstance(IWallpaperManager service, Context context, Handler handler) {
            return new HwWallpaperManager(service, context, handler);
        }
    }

    public IHwWallpaperManager getHuaweiWallpaperManager() {
        return new HwWallpaperManagerImpl();
    }

    public IHwNetworkPolicyManager getHwNetworkPolicyManager() {
        return new HwNetworkPolicyManagerImpl();
    }

    public LogException getLogException() {
        return HwLogExceptionInner.getInstance();
    }

    public PasswordUtil getPasswordUtil() {
        return HwPasswordUtil.getInstance();
    }

    public HwLocalePickerManager getHwLocalePickerManager() {
        return HwLocalePickerManagerImpl.getDefault();
    }

    public HwPCManager getHwPCManager() {
        return HwPCManagerImpl.getDefault();
    }

    public HwBaseInnerSmsManager getHwBaseInnerSmsManager() {
        return HwBaseInnerSmsManagerImpl.getDefault();
    }

    public HwInnerTelephonyManager getHwInnerTelephonyManager() {
        return HwInnerTelephonyManagerImpl.getDefault();
    }

    public HwKeyguardManager getHwKeyguardManager() {
        return HwKeyguardManagerImpl.getDefault();
    }

    public HwInnerWifiManager getHwInnerWifiManager() {
        return HwInnerWifiManagerImpl.getDefault();
    }

    public HwInnerWifiP2pManager getHwInnerWifiP2pManager() {
        return HwInnerWifiP2pManagerImpl.getDefault();
    }

    public IHwSystemManager getHwSystemManager() {
        return new HwSystemManagerImpl();
    }

    public HwWidgetManager getHwWidgetManager() {
        return HwWidgetManagerImpl.getDefault();
    }

    public IHwMediaStore getHwMediaStoreManager() {
        return HwMediaStoreImpl.getDefault();
    }

    public HwMtpDatabaseManager getHwMtpDatabaseManager() {
        return HwMtpDatabaseImpl.getDefault();
    }

    public HwMediaScannerManager getHwMediaScannerManager() {
        return HwMediaScannerImpl.getDefault();
    }

    public Interpolator createHwInterpolator(String name, Context c, AttributeSet attrs) {
        return new AnimUtil().getCubicBezierInterpolator(name, c, attrs);
    }

    public Interpolator createHwInterpolator(String name, Resources res, Theme theme, AttributeSet attrs) {
        return new AnimUtil().getCubicBezierInterpolator(name, res, theme, attrs);
    }

    public HwAnimationManager getHwAnimationManager() {
        return HwAnimationManagerImpl.getDefault();
    }

    public HwSettingsManager getHwSettingsManager() {
        return HwSettingsManagerImpl.getDefault();
    }

    public IHwThemeManagerFactory getHwThemeManagerFactory() {
        return new HwThemeManagerFactoryImpl();
    }

    public IHwGalleryCacheManagerFactory getHwGalleryCacheManagerFactory() {
        return new HwGalleryCacheManagerFactoryImpl();
    }

    public IHwConfiguration getHwConfiguration() {
        return new ConfigurationEx();
    }

    public HwFlogManager getHwFlogManager() {
        return HwFlogManagerImpl.getDefault();
    }

    public IHwCoverManager getCoverManager() {
        return CoverManager.getDefault();
    }

    public HwDrmManager getHwDrmManager() {
        return HwDrmManagerImpl.getDefault();
    }

    public Intent getHuaweiChooserIntentImpl() {
        return new Intent(ACTION_HW_CHOOSER);
    }

    public String getHuaweiResolverActivityImpl(Context context) {
        return context.getResources().getString(33685937);
    }

    public IHwWallpaperInfoStub getHwWallpaperInfoStub(WallpaperInfo ai) {
        return new HwWallpaperInfoStubImpl(ai);
    }

    public HwPackageManager getHwApplicationPackageManager() {
        return HwApplicationPackageManager.getDefault();
    }

    public HwViewRootImpl getHwViewRootImpl() {
        return HwViewRootImpl.getDefault();
    }

    public HwPackageParser getHwPackageParser() {
        return HwPackageParser.getDefault();
    }

    public PressGestureDetector getPressGestureDetector(Context context, FrameLayout docerView, Context contextActivity) {
        return new PressGestureDetector(context, docerView, contextActivity);
    }

    public IHwPowerProfileManager getHwPowerProfileManager() {
        return HwPowerProfileManagerImpl.getDefault();
    }

    public IHwMediaRecorder getHwMediaRecorder() {
        return HwMediaRecorderImpl.getDefault();
    }

    public IHwAudioRecord getHwHwAudioRecord() {
        return HwAudioRecordImpl.getDefault();
    }

    public HwApsImpl getHwApsImpl() {
        return HwApsImpl.getDefault();
    }

    public HwAppInnerBoostImpl getHwAppInnerBoostImpl() {
        return HwAppInnerBoostImpl.getDefault();
    }

    public HwInnerConnectivityManager getHwInnerConnectivityManager() {
        return HwInnerConnectivityManagerImpl.getDefault();
    }

    public HwSysResource getHwResource(int resourceType) {
        return HwSysResImpl.getResource(resourceType);
    }

    public IZrHung getZrHung(String wpName) {
        return ZrHungImpl.getZrHung(wpName);
    }

    public IAppEyeUiProbe getAppEyeUiProbe() {
        try {
            return AppEyeUiProbe.get();
        } catch (Throwable e) {
            Log.e(TAG, "Get AppEyeUiProbe encounter error", e);
            return null;
        }
    }

    public HwInnerNetworkManager getHwInnerNetworkManager() {
        return HwInnerNetworkManagerImpl.getDefault();
    }

    public HwInputMethodService getHwInputMethodService(Context context) {
        return HwInputMethodService.getInstance(context);
    }

    public HwSecImmHelper getSecImmHelper(IInputMethodManager service) {
        return new HwSecImmHelper(service);
    }

    public IHwDeviceManager getHuaweiDevicePolicyManagerImpl() {
        return new HwDeviceManagerImpl();
    }

    public IHwInnerLocationManager getHwInnerLocationManager() {
        return HwInnerLocationManagerImpl.getDefault();
    }

    public HwPerformance getHwPerformance() {
        return HwPerformanceImpl.getDefault();
    }

    public IHwMediaMonitor getHwMediaMonitor() {
        return HwMediaMonitorImpl.getDefault();
    }

    public HwNotificationResourceImpl getHwNotificationResource() {
        return new HwNotificationResourceImpl();
    }

    public IFreezeScreenApplicationMonitor getAppFreezeScreenMonitor() {
        return FreezeScreenApplicationMonitor.getInstance();
    }

    public IVRSystemServiceManager getVRSystemServiceManager() {
        return VRSystemServiceManager.getInstance();
    }

    public HwAdCleaner getHwAdCleaner() {
        return HwAdCleaner.getDefault();
    }

    public IHwDeviceAdminInfo getHwDeviceAdminInfo(Context context, ActivityInfo activityInfo) {
        return new HwDeviceAdminInfo(context, activityInfo);
    }

    public HwFrameworkMonitor getHwFrameworkMonitor() {
        return HwFrameworkMonitorImpl.getInstance();
    }

    public HwActivitySplitterImpl getHwActivitySplitterImpl(Activity a, boolean isBase) {
        return HwActivitySplitterImpl.getDefault(a, isBase);
    }

    public IHwHandler getHwHandler() {
        return HwHandlerImpl.getDefault();
    }

    public IHwView getHwView() {
        return HwViewImpl.getDefault();
    }

    public IApsManager getApsManager() {
        return HwApsManager.getDefault();
    }

    public IHwFeatureLoader getHwFeatureLoader() {
        return new HwFeatureLoader();
    }

    public HwApiCacheMangerEx getHwApiCacheManagerEx() {
        return HwApiCacheMangerEx.getDefault();
    }

    public ClassLoader getHwFLClassLoaderParent(String dexPath) {
        return HwFLClassLoader.getHwFLClassLoaderParent(dexPath);
    }

    public void updateImsServiceConfig(Context context, int subId, boolean force) {
        HwImsManagerInner.updateImsServiceConfig(context, subId, force);
    }

    public IForceRotationManager getForceRotationManager() {
        return HwForceRotationManager.getDefault();
    }

    public IHwPCResourcesUtils getHwPCResourcesUtils(AssetManager assetManager) {
        return HwPCResourcesUtils.getDefault(assetManager);
    }

    public HwActivityThreadImpl getHwActivityThread() {
        return HwActivityThreadImpl.getDefault();
    }

    public IHwCommBoosterServiceManager getHwCommBoosterServiceManager() {
        return HwCommBoosterServiceManager.getInstance();
    }

    public IHwMnoteInterface getHwMnoteInterface() {
        return new HwMnoteInterfaceImpl();
    }

    public IHwCarrierConfigPolicy getHwCarrierConfigPolicy() {
        return HwCarrierConfigPolicy.getDefault();
    }

    public IHwBehaviorCollectManager getHwBehaviorCollectManager() {
        return HwBehaviorCollectManagerImpl.getDefault();
    }

    public IHwGetCfgFileConfig getHwCfgFileConfig() {
        return HwGetCfgFile.getDefault();
    }

    public HwOptPackageParser getHwOptPackageParser() {
        return new HwOptPackageParserImpl();
    }

    public IIndexSearchManager getIndexSearchManager() {
        return IndexSearchManager.getInstance();
    }

    public IIndexSearchParser getIndexSearchParser() {
        return IndexSearchParser.getInstance();
    }

    public IIndexClearManager getIndexClearManager() {
        return IndexClearManager.getInstance();
    }

    public IHwAutofillHelper getHwAutofillHelper() {
        return new HwAutofillHelper();
    }

    public IAwareBitmapCacher getHwIAwareBitmapCacher() {
        return AwareBitmapCacher.getDefault();
    }

    public IHwDisplayManagerGlobalEx getHwDisplayManagerGlobalEx(IHwDisplayManagerGlobalInner dmg) {
        return new HwDisplayManagerGlobalEx(dmg);
    }
}
