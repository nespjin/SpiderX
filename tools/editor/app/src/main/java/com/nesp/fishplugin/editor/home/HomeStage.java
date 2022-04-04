package com.nesp.fishplugin.editor.home;

import com.nesp.fishplugin.core.Environment;
import com.nesp.fishplugin.core.Result;
import com.nesp.fishplugin.core.data.Plugin;
import com.nesp.fishplugin.core.data.Plugin2;
import com.nesp.fishplugin.editor.AppInfo;
import com.nesp.fishplugin.editor.R;
import com.nesp.fishplugin.editor.StageHomeViewBinding;
import com.nesp.fishplugin.editor.app.*;
import com.nesp.fishplugin.editor.plugin.MoviePluginBuilder;
import com.nesp.fishplugin.editor.plugin.OnBuildProgressListener;
import com.nesp.fishplugin.editor.plugin.PluginBuilder;
import com.nesp.fishplugin.editor.project.NewProjectWizardDialog;
import com.nesp.fishplugin.editor.project.Project;
import com.nesp.fishplugin.editor.project.ProjectManager;
import com.nesp.fishplugin.editor.utils.DragResizer;
import com.nesp.sdk.java.lang.SingletonFactory;
import com.nesp.sdk.java.util.OnResultListener;
import com.nesp.sdk.javafx.platform.PlatformUtil;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/12/2 上午9:06
 * Description:
 **/
public class HomeStage extends AppBaseStage {

    private final Logger logger = LogManager.getLogger(HomeStage.class);
    private MenuItem closeProjectMenuItem;

    private HomeStage() {
        //no instance
    }

    private WeakReference<StageHomeViewBinding> mBinding;

    @NotNull
    private StageHomeViewBinding getBinding() {
        if (mBinding == null || mBinding.get() == null) {
            mBinding = new WeakReference<>(StageHomeViewBinding.inflate(R.layout.stage_home));
        }
        return Objects.requireNonNull(mBinding.get());
    }

    private WeakReference<HomeStateViewModel> viewModel;

    private HomeStateViewModel getViewModel() {
        if (viewModel == null || viewModel.get() == null) {
            HomeStateViewModel homeStateViewModel = new HomeStateViewModel() {
                private final StageHomeViewBinding binding = getBinding();

                @Override
                @SuppressWarnings("unchecked")
                void onWorkingProjectInvalidate(Project project) {
                    // On Project Changed
                    invalidateProjectView();
                    setTitle(project);
                    // stop build at first
                    buildState(PluginBuilder.BUILD_STATUS_STOP);

                    if (project != null) {
                        startWatchProjectDir();

                        Optional<Project> optionalProject = Optional.of(project);
                        optionalProject.map(Project::getTargetPlugin).map(Plugin2::getType).ifPresent(pluginType -> {
                            if (pluginType == Plugin.TYPE_MOVIE) {
                                pluginBuilder = MoviePluginBuilder.getInstance();
                            } else {
                                new AppAlert(Alert.AlertType.WARNING, "不支持该项目",
                                        ButtonType.OK)
                                        .showAndWait();
                                closeProject();
                                return;
                            }
                            binding.cbBuildType.getItems().clear();
                            binding.cbBuildType.getItems()
                                    .addAll(Arrays.asList(pluginBuilder.getBuildTaskDisplayNames()));
                            binding.cbBuildType.getSelectionModel().select(0);
                        });

                        optionalProject.map(Project::getTargetPlugin).ifPresent(targetPlugin -> {
                            binding.cbDeviceType.getItems().clear();
                            if (targetPlugin.isSupportMobilePhone()) {
                                binding.cbDeviceType.getItems().add("Mobile");
                            }
                            if (targetPlugin.isSupportTable()) {
                                binding.cbDeviceType.getItems().add("Table");
                            }
                            if (targetPlugin.isSupportDesktop()) {
                                binding.cbDeviceType.getItems().add("Desktop");
                            }

                            if (binding.cbDeviceType.getItems().size() == 0) {
                                new AppAlert(Alert.AlertType.WARNING, "不受支持的设备类型",
                                        ButtonType.OK)
                                        .showAndWait();
                                closeProject();
                                return;
                            }

                            binding.cbDeviceType.getSelectionModel().select(0);
                        });
                    } else {
                        pluginBuilder = null;
                        binding.cbBuildType.getItems().clear();
                        binding.cbDeviceType.getItems().clear();
                    }

                    hasFileOpened(false);
                }

                @Override
                void onBottomStatusInvalidate(String status) {
                    binding.lbBottomStatus.setText(status);
                }
            };
            viewModel = new WeakReference<>(homeStateViewModel);
        }
        return Objects.requireNonNull(viewModel.get());
    }

    private PluginBuilder pluginBuilder;

    private IntegerProperty buildState;

    public IntegerProperty buildStateProperty() {
        if (buildState == null) {
            buildState = new SimpleIntegerProperty() {
                @Override
                protected void invalidated() {
                    int buildState = get();

                    HomeStateViewModel viewModel = getViewModel();
                    viewModel.bottomStatus(PluginBuilder.getBuildStatusString(buildState));

                    StageHomeViewBinding binding = getBinding();

                    binding.ivBuildStop.setDisable(true);
                    binding.ivBuildStart.setDisable(false);

                    switch (buildState) {
                        case PluginBuilder.BUILD_STATUS_NONE -> {
                            binding.vbBottom.getChildren().remove(binding.vbBuildOutput);
//                            binding.vbTop.getChildren().remove(binding.apBuildProgressContainer);
                            binding.apBuildProgressContainer.setVisible(false);
                            getViewModel().bottomStatus("");
                        }

                        case PluginBuilder.BUILD_STATUS_START -> {
                            if (pluginBuilder != null) {
                                OnResultListener<Boolean> saveResultListener = (isSuccess) -> {
                                    if (!isSuccess) {
                                        return;
                                    }
                                    binding.textFlowBuildOutput.getChildren().clear();
                                    int selectedIndex = binding.cbBuildType.getSelectionModel().getSelectedIndex();
                                    OnBuildProgressListener onBuildProgressListener = new OnBuildProgressListener() {
                                        @Override
                                        public void onProgress(double progress, int lineType, String line) {
                                            if (!line.isEmpty()) {
                                                Text text = new Text(line + "\n");
                                                String lineColor;
                                                if (lineType == -1) {
                                                    lineColor = "#ff0000";
                                                } else if (lineType == 1) {
                                                    lineColor = "#ffcc00";
                                                } else if (lineType == 2) {
                                                    lineColor = "#00bf00";
                                                } else {
                                                    lineColor = "#000";
                                                }
                                                text.setFill(Color.valueOf(lineColor));
                                                binding.textFlowBuildOutput.getChildren().add(text);
                                                binding.spBuildOutput.setVvalue(binding.spBuildOutput.getVmax());
                                            }

                                            if (progress > -2) {
                                                binding.pbBuild.setProgress(progress);
                                            }

                                            if (progress == 1 || progress < -2) {
                                                try {
                                                    Thread.sleep(50);
                                                } catch (InterruptedException e) {
                                                    Thread.currentThread().interrupt();
                                                }
                                                binding.ivCloseBuildOutput.setVisible(true);
//                                                binding.vbTop.getChildren().remove(binding.apBuildProgressContainer);
                                                binding.apBuildProgressContainer.setVisible(false);
                                                if (progress < -2) {
                                                    buildState(PluginBuilder.BUILD_STATUS_FAILED);
                                                } else {
                                                    buildState(PluginBuilder.BUILD_STATUS_SUCCESS);
                                                }
                                            }
                                        }
                                    };
                                    buildState(PluginBuilder.BUILD_STATUS_BUILDING);
                                    pluginBuilder.build(selectedIndex, onBuildProgressListener);
                                };
                                saveOpenedFile(saveResultListener);
                            } else {
                                buildState(PluginBuilder.BUILD_STATUS_STOP);
                            }
                        }

                        case PluginBuilder.BUILD_STATUS_BUILDING -> {
                            binding.ivBuildStop.setDisable(false);
                            binding.ivBuildStart.setDisable(true);

                            if (!binding.vbBottom.getChildren().contains(binding.vbBuildOutput)) {
                                binding.textFlowBuildOutput.getChildren().clear();
                                binding.vbBottom.getChildren().add(0, binding.vbBuildOutput);
                            }

//                            if (!binding.vbTop.getChildren().contains(binding.apBuildProgressContainer)) {
//                                binding.vbTop.getChildren().add(1, binding.apBuildProgressContainer);
//                            }
                            binding.apBuildProgressContainer.setVisible(true);
                            binding.ivCloseBuildOutput.setVisible(false);
                        }

                        case PluginBuilder.BUILD_STATUS_STOP -> {
                            if (pluginBuilder != null) {
                                pluginBuilder.stopBuild(new OnResultListener<Boolean>() {
                                    @Override
                                    public void onResult(Boolean result) {
                                        buildState(PluginBuilder.BUILD_STATUS_NONE);
                                    }
                                });
                            } else {
                                buildState(PluginBuilder.BUILD_STATUS_NONE);
                            }
                        }

                        case PluginBuilder.BUILD_STATUS_FAILED -> {
                            viewModel.bottomStatus("构建失败");
                        }

                        case PluginBuilder.BUILD_STATUS_SUCCESS -> {
                            viewModel.bottomStatus("构建结束");
                        }

                        default -> throw new IllegalStateException("Unexpected value: " + buildState);
                    }
                }
            };
        }
        return buildState;
    }

    public Integer buildState() {
        return buildStateProperty().get();
    }

    public void buildState(Integer value) {
        buildStateProperty().set(value);
    }

    private Thread watchProjectDirThread;

    private void stopWatchProjectDir() {
        if (watchProjectDirThread != null) {
            watchProjectDirThread.interrupt();
            watchProjectDirThread = null;
        }
    }

    private void startWatchProjectDir() {
        Project workingProject = ProjectManager.getInstance().getWorkingProject();
        if (workingProject == null) return;
        File rootDirectory = workingProject.getRootDirectory();
        if (rootDirectory == null) return;
        if (watchProjectDirThread != null) return;
        watchProjectDirThread = new Thread(() -> {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                Path path = FileSystems.getDefault().getPath(rootDirectory.getAbsolutePath());
                path.register(watchService,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE);

                for (; ; ) {
                    watchService.take();
                    if (Thread.currentThread().isInterrupted()) break;
                    Platform.runLater(this::loadProjectTreeView);
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        watchProjectDirThread.setDaemon(true);
        watchProjectDirThread.start();
    }

    private static boolean isShown = false;

    public static void showWindow() {
        if (isShown) return;
        var shared =
                SingletonFactory.getWeakInstance(HomeStage.class, HomeStage::new);
        shared.show();
        isShown = true;
    }

    @Override
    public void onCreate(final @NotNull Stage stage) {
        super.onCreate(stage);
        stage.setMinWidth(1020);
        stage.setMinHeight(700);
        AppInfo.name = getResource().getString(R.string.app_name);
        initializeViews();
        stage.setOpacity(0);
        getStage().focusedProperty().addListener((observable, oldValue, newValue) -> onFocusedChanged(newValue));

        /*OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(new Request.Builder().get().url("https://www.baidu.com").build()).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                logger.error("error when request net", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                logger.info("request net result " + response.toString());
            }
        });*/
    }

    @SuppressWarnings("unchecked")
    public void onFocusedChanged(boolean isFocused) {
        if (isFocused && currentFileOpened != null) {
            StageHomeViewBinding binding = getBinding();
            ObservableList<TreeItem<File>> selectedItems = binding.dirTreeView.getSelectionModel().getSelectedItems();
            if (selectedItems != null && !selectedItems.isEmpty()) {
                File value = selectedItems.get(0).getValue();
                double scrollTop = binding.taFileEditor.getScrollTop();

                if (value.isFile()) {
                    openFile(currentFileOpened, new OnResultListener<Boolean>() {
                        @Override
                        public void onResult(Boolean result) {
                            binding.taFileEditor.setScrollTop(scrollTop);
                        }
                    });
                }
            }
        } else {
            // When window lost focus, try to save file current opened
            saveOpenedFile();
        }
    }

    @Override
    public void onShown(WindowEvent event) {
        super.onShown(event);
        LauncherStage.hideWindow();

        new Transition() {

            {
                setCycleDuration(new Duration(300));
            }

            @Override
            protected void interpolate(double frac) {
                getStage().setOpacity(frac);
            }
        }.play();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Initialize
    ///////////////////////////////////////////////////////////////////////////

    private void initializeViews() {
        StageHomeViewBinding binding = getBinding();
        setContent(binding.getRoot());
        setTitle((Project) null);
        initializeView();
    }

    private void setTitle(Project project) {
        String title = AppInfo.name;
        if (project != null) {
            title += " - " + project.getName();
        }
        setTitle(title);
    }

    @SuppressWarnings("unchecked")
    private void initializeView() {
        initializeTopMenu();

        StageHomeViewBinding binding = getBinding();

        binding.cbDeviceType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ProjectManager.switchDeviceType(ProjectManager.getInstance().getWorkingProject(), getSelectedDeviceType());
                if (currentFileOpened != null) {
                    openFile(currentFileOpened);
                }
            }
        });

        binding.cbBuildType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                binding.cbDeviceType.setVisible(!newValue.equals(MoviePluginBuilder.getInstance().getBuildPluginTask().name()));
            }
        });

        binding.ivBuildStart.disableProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                binding.ivBuildStart.setImage(
                        new Image("/drawable/ic_start_build" + (newValue ? "_disable" : "") + ".png"));
            }
        });
        binding.ivBuildStart.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    buildState(PluginBuilder.BUILD_STATUS_START);
                }
            }
        });

        binding.ivBuildStop.disableProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                binding.ivBuildStop.setImage(
                        new Image("/drawable/ic_stop_build" + (newValue ? "_disable" : "") + ".png"));
            }
        });
        binding.ivBuildStop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    buildState(PluginBuilder.BUILD_STATUS_STOP);
                }
            }
        });

        TreeView<File> dirTreeView = ((TreeView<File>) binding.dirTreeView);
        dirTreeView.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<File> call(TreeView<File> param) {
                return new TextFieldTreeCell<>() {
                    @Override
                    public void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        setContentDisplay(ContentDisplay.TEXT_ONLY);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        });
        dirTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            File value = newValue.getValue();
            if (value.isFile())
                openFile(value);
            else {
                newValue.getChildren().clear();
                addTreeViewItem(value, newValue, false);
            }

        });

        initializeEditor();

//        binding.vbBottom.setOnMouseMoved(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                binding.vbBottom
//            }
//        });
        DragResizer.makeResizable(binding.textFlowBuildOutput);

        binding.ivCloseBuildOutput.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    binding.vbBottom.getChildren().remove(binding.vbBuildOutput);
                }
            }
        });

        binding.textFlowBuildOutput.maxWidthProperty().bind(binding.vbBottom.widthProperty());

        invalidateView();

        HomeStateViewModel viewModel = getViewModel();

        viewModel.onBottomStatusInvalidate(null);
        viewModel.onWorkingProjectInvalidate(null);
    }

    private int getSelectedDeviceType() {
        StageHomeViewBinding binding = getBinding();
        switch (binding.cbDeviceType.getSelectionModel().getSelectedIndex()) {
            case 0:
                return Environment.DEVICE_TYPE_MOBILE_PHONE;
            case 1:
                return Environment.DEVICE_TYPE_TABLE;
            case 2:
                return Environment.DEVICE_TYPE_DESKTOP;
            default:
                return -1;
        }
    }

    private void initializeTopMenu() {
        StageHomeViewBinding binding = getBinding();

        // Menu File
        final Menu newProjectMenu = new Menu("新建项目(_N)");
        newProjectMenu.setMnemonicParsing(true);
        final MenuItem newProjectMenuMovieItem = new MenuItem("小丑鱼影视");
        newProjectMenuMovieItem.setOnAction(event -> {
            showNewProjectWizardDialog();
        });
        newProjectMenu.getItems().add(newProjectMenuMovieItem);
        /* final MenuItem newProjectMenuMusicItem = new MenuItem("Music");
        newProjectMenuMusicItem.setOnAction(new WeakEventHandler<>(event -> {

        }));
        newProjectMenu.getItems().add(newProjectMenuMusicItem);
        final MenuItem newProjectMenuBookItem = new MenuItem("Book");
        newProjectMenuBookItem.setOnAction(new WeakEventHandler<>(event -> {

        }));
        newProjectMenu.getItems().add(newProjectMenuBookItem);*/
        binding.topMenuFile.getItems().add(newProjectMenu);

        final MenuItem openProjectMenuItem = new MenuItem("打开项目(_O)");
        openProjectMenuItem.setMnemonicParsing(true);
        openProjectMenuItem.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(Storage.getProjectsDir());
            directoryChooser.setTitle("选择项目");
            File file = directoryChooser.showDialog(getStage());
            WorkingDialog<Result<Project>> workingDialog =
                    new WorkingDialog<>(() -> ProjectManager.openProject(file));
            workingDialog.setTitle("正在打开项目...");
            workingDialog.setOnFinishListener((openProjectResult) -> {
                if (openProjectResult.getCode() == Result.CODE_SUCCESS) {
                    getViewModel().workingProject(openProjectResult.getData());
                } else {
                    String message = openProjectResult.getMessage();
                    if (!message.isEmpty())
                        getViewModel().bottomStatus(message);
                }
            });
            getViewModel().bottomStatus("正在打开项目...");
            workingDialog.run();
        });
        binding.topMenuFile.getItems().add(openProjectMenuItem);

        closeProjectMenuItem = new MenuItem("关闭项目(_C)");
        closeProjectMenuItem.setMnemonicParsing(true);
        closeProjectMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeProject();
            }
        });
        binding.topMenuFile.getItems().add(closeProjectMenuItem);

        binding.topMenuFile.getItems().add(new SeparatorMenuItem());

       /* final MenuItem settingsMenuItem = new MenuItem("Settings");
        settingsMenuItem.setOnAction(new WeakEventHandler<>(event -> {
        }));
        binding.topMenuFile.getItems().add(settingsMenuItem);*/

        binding.topMenuFile.getItems().add(new SeparatorMenuItem());

        final MenuItem exitMenuItem = new MenuItem("退出");
        exitMenuItem.setOnAction(new WeakEventHandler<>(event -> {
            Platform.exit();
        }));
        binding.topMenuFile.getItems().add(exitMenuItem);

        // Menu Help
        final MenuItem aboutMenuItem = new MenuItem("关于 " + AppInfo.name);
        aboutMenuItem.setOnAction(event -> {
            showAboutDialog();
        });
        binding.topMenuHelp.getItems().add(aboutMenuItem);
    }

    private void closeProject() {
        // do other
        getViewModel().workingProject(new Project());
        getViewModel().workingProject(null);
    }

    private void showNewProjectWizardDialog() {
        new NewProjectWizardDialog(Plugin.TYPE_MOVIE).showAndWait().ifPresent(project -> {
            if (Optional.ofNullable(project.getProjectManifestFile()).map(File::exists).orElse(false)) {
                new Alert(Alert.AlertType.WARNING, "该项目已存在", ButtonType.OK, ButtonType.CANCEL)
                        .showAndWait();
                return;
            }
            WorkingDialog<Boolean> workingDialog = new WorkingDialog<>(() -> {
                try {
                    return ProjectManager.initializeProject(project);
                } catch (Exception e) {
                    LogManager.getLogger(HomeStage.class).error("initializeProject", e);
                    return false;
                }
            });
            workingDialog.setTitle("创建中...");
            workingDialog.setOnFinishListener((r) -> {
                if (!r) {
                    getViewModel().bottomStatus("创建项目失败");
                    return;
                }
                getViewModel().workingProject(project);
                getViewModel().bottomStatus("");
            });
            getViewModel().bottomStatus("正在创建项目(" + project.getName() + ")...");
            workingDialog.run();
        });
    }

    private void showAboutDialog() {
        AboutDialog aboutDialog = new AboutDialog();
        aboutDialog.showAndWait();
    }

    private void invalidateView() {
        invalidateProjectView();
    }

    @SuppressWarnings("unchecked")
    private void invalidateProjectView() {
        StageHomeViewBinding binding = getBinding();
        final ProjectManager manager = ProjectManager.getInstance();
        Project workingProject = manager.getWorkingProject();
        if (workingProject == null) {
            // No Project opened
            if (closeProjectMenuItem != null) {
                closeProjectMenuItem.setDisable(true);
            }
            binding.dirTreeView.setRoot(null);
            binding.borderPanelContent.setLeft(null);
            binding.vbTop.getChildren().remove(binding.topToolBar);

            hasFileOpened(false);

        } else {
            // Project Opened
            if (closeProjectMenuItem != null) {
                closeProjectMenuItem.setDisable(false);
            }
            if (binding.borderPanelContent.getLeft() == null) {
                binding.borderPanelContent.setLeft(binding.dirTreeView);
            }
            if (!binding.vbTop.getChildren().contains(binding.topToolBar)) {
                binding.vbTop.getChildren().add(binding.topToolBar);
            }
        }
        loadProjectTreeView();
    }

    ///////////////////////////////////////////////////////////////////////////
    // File TreeView
    ///////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    private void loadProjectTreeView() {
        getBinding().dirTreeView.setRoot(null);

        final ProjectManager manager = ProjectManager.getInstance();
        Project workingProject = manager.getWorkingProject();
        if (workingProject == null) return;
        File rootDirectory = workingProject.getRootDirectory();
        if (rootDirectory == null) return;
        File[] files = rootDirectory.listFiles();
        if (files == null) return;

        TreeView<File> dirTreeView = ((TreeView<File>) getBinding().dirTreeView);
        TreeItem<File> rootTreeItem = new TreeItem<>(rootDirectory);

        rootTreeItem.setExpanded(true);
        addTreeViewItem(rootDirectory, rootTreeItem, false);
        dirTreeView.setRoot(rootTreeItem);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemOpenWithVsCode = new MenuItem();
        EventHandler<ActionEvent> menuItemOpenWithVsCodeEventHandler = new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {

                MultipleSelectionModel<TreeItem<File>> selectionModel = dirTreeView.getSelectionModel();
                ObservableList<TreeItem<File>> selectedItems = selectionModel.getSelectedItems();
                if (selectedItems == null || selectedItems.isEmpty()) return;
                TreeItem<File> fileTreeItem = selectedItems.get(0);

                Runnable runnableMain = new Runnable() {
                    @Override
                    public void run() {
                        getViewModel().bottomStatus("使用VsCode打开失败!");
                    }
                };
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            var windows = com.nesp.sdk.javafx.platform.Platform.WINDOWS;
                            boolean isWin = PlatformUtil.getPlatform() == windows;
                            String findCodeCommand = isWin ? "where" : "which";
                            findCodeCommand = findCodeCommand + " code";
                            Process execFindCodeCommand = Runtime.getRuntime().exec(findCodeCommand);

                            boolean isSuccess = true;
                            if (execFindCodeCommand.waitFor() != 0) {
                                isSuccess = false;
                            } else {
                                String codePath;
                                try (InputStreamReader inputStreamReader =
                                             new InputStreamReader(execFindCodeCommand.getInputStream());
                                     BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                                    codePath = bufferedReader.readLine();
                                }

                                logger.info("VsCode Path: {}", codePath);

                                if (codePath == null || codePath.isEmpty()) {
                                    isSuccess = false;
                                } else {
                                    Process exec = Runtime.getRuntime().exec(new String[]{
                                            isWin ? "cmd" : "",
                                            isWin ? "/c" : "",
                                            codePath, fileTreeItem.getValue().getAbsolutePath()
                                    }, null, null);
                                    if (exec.waitFor() != 0) {
                                        isSuccess = false;
                                    }
                                }
                            }

                            if (!isSuccess) {
                                runOnUIThread(runnableMain);
                            }
                            logger.info("Open In VsCode Success");
                        } catch (IOException | InterruptedException e) {
                            logger.error("Error occurs when opening file in VsCode ", e);
                            runOnUIThread(runnableMain);
                        }
                    }
                };
                runOnIOThread(true, runnable);
            }
        };
        menuItemOpenWithVsCode.setOnAction(menuItemOpenWithVsCodeEventHandler);
        menuItemOpenWithVsCode.setText("使用VsCode打开");
        contextMenu.getItems().add(menuItemOpenWithVsCode);

        String menuItemText = "使用文件管理器打开";
        String commandExec = "";
        switch (PlatformUtil.getPlatform()) {
            case WINDOWS -> {
                menuItemText = "使用Explorer打开";
                commandExec = "explorer";
            }
            case MAC_OSX -> {
                menuItemText = "使用Finder打开";
                commandExec = "open";
            }
        }

        if (!commandExec.isEmpty()) {
            MenuItem menuItemOpenWithSystemFileManager = new MenuItem();
            menuItemOpenWithSystemFileManager.setText(menuItemText);
            String finalCommandExec = commandExec;
            menuItemOpenWithSystemFileManager.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    TreeItem<File> fileTreeItem = getSelectedFirstFileTreeItem(dirTreeView);
                    if (fileTreeItem == null) return;
                    File value = fileTreeItem.getValue();
                    try {
                        Runtime.getRuntime().exec(new String[]{finalCommandExec, value.getAbsolutePath()});
                    } catch (IOException e) {
                        getViewModel().bottomStatus("打开失败");
                    }
                }
            });
            contextMenu.getItems().add(menuItemOpenWithSystemFileManager);
        }

        MenuItem menuItemItemDelete = new MenuItem();
        menuItemItemDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TreeItem<File> fileTreeItem = getSelectedFirstFileTreeItem(dirTreeView);
                if (fileTreeItem == null) return;
                File value = fileTreeItem.getValue();
                Alert alert = new AppAlert(Alert.AlertType.WARNING,
                        String.format("是否删除%s?", value.getName()),
                        ButtonType.OK, ButtonType.CANCEL);
                Optional<ButtonType> buttonType = alert.showAndWait();
                if (buttonType.orElse(ButtonType.CANCEL) != ButtonType.OK) return;
                TreeItem<File> parent = fileTreeItem.getParent();
                if (!value.delete()) {
                    getViewModel().bottomStatus("删除失败");
                } else {
                    getViewModel().bottomStatus("删除成功");
                    if (parent != null) parent.getChildren().remove(fileTreeItem);
                }
            }
        });
        menuItemItemDelete.setText("删除");
        contextMenu.getItems().add(menuItemItemDelete);

        dirTreeView.setContextMenu(contextMenu);
    }

    @Nullable
    private TreeItem<File> getSelectedFirstFileTreeItem(TreeView<File> dirTreeView) {
        MultipleSelectionModel<TreeItem<File>> selectionModel = dirTreeView.getSelectionModel();
        ObservableList<TreeItem<File>> selectedItems = selectionModel.getSelectedItems();
        if (selectedItems == null || selectedItems.isEmpty()) return null;
        return selectedItems.get(0);
    }

    private void addTreeViewItem(File currentFile, TreeItem<File> currentTreeItem, boolean isRecursive) {
        File[] files = currentFile.listFiles();
        if (files == null) return;
        for (File childFile : files) {
            Project workingProject = ProjectManager.getInstance().getWorkingProject();
            if (workingProject != null) {
                if (childFile.getAbsolutePath().equals(workingProject.getBuildDirectory().getAbsolutePath())) {
                    continue;
                }
            }
            TreeItem<File> item = new TreeItem<>(childFile);
            currentTreeItem.getChildren().add(item);
            if (!childFile.isFile() && isRecursive) {
                item.setExpanded(true);
                addTreeViewItem(childFile, item, true);

            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // Open File
    ///////////////////////////////////////////////////////////////////////////

    private BooleanProperty hasFileOpened;

    public BooleanProperty hasFileOpenedProperty() {
        if (hasFileOpened == null) {
            hasFileOpened = new SimpleBooleanProperty(true) {
                @Override
                protected void invalidated() {
                    StageHomeViewBinding binding = getBinding();
                    if (get()) {
                        if (binding.borderPanelContent.getCenter() == null) {
                            binding.borderPanelContent.setCenter(binding.taFileEditor);
                        }
                    } else {
                        currentFileOpened = null;
                        binding.taFileEditor.clear();
                        binding.borderPanelContent.setCenter(null);
                    }
                }
            };
        }
        return hasFileOpened;
    }

    public Boolean hasFileOpened() {
        return hasFileOpenedProperty().get();
    }

    public void hasFileOpened(Boolean value) {
        hasFileOpenedProperty().set(value);
    }

    private File currentFileOpened = null;

    private void openFile(File file) {
        openFile(file, null);
    }

    private void openFile(File file, OnResultListener<Boolean> onResultListener) {
        if (file == null || !file.exists()) return;

        WorkingDialog<String> workingDialog = new WorkingDialog<>(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                StringBuilder fileContent = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {
                    fileContent.append(str);
                    fileContent.append("\n");
                }
                return fileContent.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        });
        workingDialog.setOnFinishListener(s -> {
            getViewModel().bottomStatus("");
            getBinding().taFileEditor.setText(s);
            currentFileOpened = file;
            hasFileOpened(true);
            if (onResultListener != null) {
                onResultListener.onResult(true);
            }
        });
        workingDialog.setTitle("正在打开文件 " + file.getName());
        workingDialog.run();
        getViewModel().bottomStatus("正在打开文件(" + file.getName() + ")...");
    }

    ///////////////////////////////////////////////////////////////////////////
    // The Simple Editor
    ///////////////////////////////////////////////////////////////////////////

    private void initializeEditor() {
        TextArea taFileEditor = getBinding().taFileEditor;
        KeyCodeCombination combinationSave =
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        getStage().getScene().getAccelerators().put(combinationSave, new Runnable() {
            @Override
            public void run() {
                saveOpenedFile();
            }
        });
    }

    private void saveOpenedFile() {
        saveOpenedFile(null);
    }

    @SuppressWarnings("unchecked")
    private void saveOpenedFile(OnResultListener<Boolean> onResultListener) {
        StageHomeViewBinding binding = getBinding();
        ObservableList<TreeItem<File>> selectedItems =
                binding.dirTreeView.getSelectionModel().getSelectedItems();
        if (selectedItems == null || selectedItems.isEmpty()) {
            if (onResultListener != null) {
                onResultListener.onResult(true);
            }
            return;
        }
        File file = selectedItems.get(0).getValue();
        if (!file.isFile()) {
            if (onResultListener != null) {
                onResultListener.onResult(true);
            }
            return;
        }
        String text = binding.taFileEditor.getText();
        if (text == null) {
            text = "";
        }

        String finalText = text;
        WorkingDialog<Boolean> workingDialog = new WorkingDialog<>(() -> {
            try {
                FileUtils.writeStringToFile(file, finalText, StandardCharsets.UTF_8.name());
            } catch (IOException e) {
                return false;
            }
            return true;
        });
        workingDialog.setOnFinishListener(isSaveSuccess -> {
            if (isSaveSuccess) {
                getViewModel().bottomStatus("");
            } else {
                getViewModel().bottomStatus(String.format("保存文件 %s 失败", file.getName()));
            }
            if (onResultListener != null) {
                onResultListener.onResult(isSaveSuccess);
            }
        });
        workingDialog.setTitle("正在保存文件 " + file.getName());
        workingDialog.run();
        getViewModel().bottomStatus("正在保存文件(" + file.getName() + ")...");
    }

    @Override
    public void onHidden(WindowEvent event) {
        super.onHidden(event);
        stopWatchProjectDir();
        Optional.ofNullable(viewModel).map(Reference::get).ifPresent(HomeStateViewModel::destroy);
        getBinding().textFlowBuildOutput.maxWidthProperty().unbind();
        closeProjectMenuItem = null;
        SingletonFactory.removeWeakInstance(HomeStage.class);
        isShown = false;
    }

}
