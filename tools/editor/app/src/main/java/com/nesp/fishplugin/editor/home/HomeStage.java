package com.nesp.fishplugin.editor.home;

import com.nesp.fishplugin.core.data.Plugin;
import com.nesp.fishplugin.editor.AppInfo;
import com.nesp.fishplugin.editor.HomeStageViewBinding;
import com.nesp.fishplugin.editor.R;
import com.nesp.fishplugin.editor.app.AppBaseStage;
import com.nesp.fishplugin.editor.app.ResultRunnable;
import com.nesp.fishplugin.editor.app.Storage;
import com.nesp.fishplugin.editor.app.WorkingDialog;
import com.nesp.fishplugin.editor.plugin.MoviePluginBuilder;
import com.nesp.fishplugin.editor.plugin.PluginBuilder;
import com.nesp.fishplugin.editor.project.NewProjectWizardDialog;
import com.nesp.fishplugin.editor.project.Project;
import com.nesp.fishplugin.editor.project.ProjectManager;
import com.nesp.sdk.java.lang.SingletonFactory;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
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

    private MenuItem closeProjectMenuItem;

    private HomeStage() {
        //no instance
    }

    private WeakReference<HomeStageViewBinding> mBinding;

    @NotNull
    private HomeStageViewBinding getBinding() {
        if (mBinding == null || mBinding.get() == null) {
            mBinding = new WeakReference<>(HomeStageViewBinding.inflate(R.layout.home_stage));
        }
        return Objects.requireNonNull(mBinding.get());
    }

    private WeakReference<HomeStateViewModel> viewModel;

    private HomeStateViewModel getViewModel() {
        if (viewModel == null || viewModel.get() == null) {
            HomeStateViewModel homeStateViewModel = new HomeStateViewModel() {
                @Override
                @SuppressWarnings("unchecked")
                void onWorkingProjectInvalidate(Project project) {
                    // On Project Changed
                    invalidateProjectView();
                    setTitle(project);

                    if (project != null) {
                        startWatchProjectDir();
                        Optional.of(project).map(Project::getTargetPlugin)
                                .map(Plugin::getType).ifPresent(integer -> {
                                    if (integer == Plugin.TYPE_MOVIE) {
                                        pluginBuilder = new MoviePluginBuilder();
                                    }
                                    HomeStageViewBinding binding = getBinding();
                                    binding.cbBuildTYpe.getItems()
                                            .addAll(Arrays.asList(pluginBuilder.getBuildTypes()));
                                });
                        buildState(PluginBuilder.BUILD_STATUS_STOP);
                        buildState(PluginBuilder.BUILD_STATUS_NONE);
                    }
                }

                @Override
                void onBottomStatusInvalidate(String status) {
                    getBinding().lbBottomStatus.setText(status);
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
                    HomeStageViewBinding binding = getBinding();

                    binding.ivBuildStop.setDisable(true);
                    binding.ivBuildStart.setDisable(false);

                    switch (buildState) {
                        case PluginBuilder.BUILD_STATUS_NONE -> {

                        }

                        case PluginBuilder.BUILD_STATUS_BUILDING -> {
                            binding.ivBuildStop.setDisable(false);
                            binding.ivBuildStart.setDisable(true);
                        }

                        case PluginBuilder.BUILD_STATUS_FAILED -> {

                        }

                        case PluginBuilder.BUILD_STATUS_SUCCESS -> {

                        }

                        case PluginBuilder.BUILD_STATUS_STOP -> {

                        }

                        default -> throw new IllegalStateException("Unexpected value: " + buildState);
                    }

                    HomeStateViewModel viewModel = getViewModel();
                    viewModel.bottomStatus(PluginBuilder.getBuildStatusString(buildState));
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
    }

    ///////////////////////////////////////////////////////////////////////////
    // Initialize
    ///////////////////////////////////////////////////////////////////////////

    private void initializeViews() {
        HomeStageViewBinding binding = getBinding();
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

        HomeStageViewBinding binding = getBinding();

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
                    buildState(PluginBuilder.BUILD_STATUS_BUILDING);
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
        dirTreeView.getSelectionModel().selectedItemProperty()
                .addListener((ChangeListener<TreeItem<File>>) (observable, oldValue, newValue) -> {
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

        invalidateView();

        binding.borderPanelContent.setCenter(null);
    }

    private void initializeTopMenu() {
        HomeStageViewBinding binding = getBinding();

        // Menu File
        final Menu newProjectMenu = new Menu("_New Project");
        newProjectMenu.setMnemonicParsing(true);
        final MenuItem newProjectMenuMovieItem = new MenuItem("Movie");
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

        final MenuItem openProjectMenuItem = new MenuItem("_Open Project");
        openProjectMenuItem.setMnemonicParsing(true);
        openProjectMenuItem.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(Storage.getProjectsDir());
            directoryChooser.setTitle("Choose Project");
            File file = directoryChooser.showDialog(getStage());
            if (file != null && file.exists() && file.isDirectory()) {
                File[] pluginManifests = file.listFiles((dir, name) -> name.contains("src"));
                if (pluginManifests != null && pluginManifests.length > 0) {
                    Project project = ProjectManager.createProject(file.getName(), Plugin.TYPE_MOVIE);
                    if (project != null) {
                        WorkingDialog<Integer> workingDialog = new WorkingDialog<>(null);
                        workingDialog.setTitle("Opening...");
                        workingDialog.setOnFinishListener((r) -> {
                            getViewModel().workingProject(project);
                            getViewModel().bottomStatus("");
                        });
                        workingDialog.show();
                        getViewModel().bottomStatus("Opening Project(" + project.getName() + ")...");
                        workingDialog.run();
                    }
                }
            }
        });
        binding.topMenuFile.getItems().add(openProjectMenuItem);

        closeProjectMenuItem = new MenuItem("_Close Project");
        closeProjectMenuItem.setMnemonicParsing(true);
        closeProjectMenuItem.setOnAction(event -> closeProject());
        binding.topMenuFile.getItems().add(closeProjectMenuItem);

        binding.topMenuFile.getItems().add(new SeparatorMenuItem());

       /* final MenuItem settingsMenuItem = new MenuItem("Settings");
        settingsMenuItem.setOnAction(new WeakEventHandler<>(event -> {
        }));
        binding.topMenuFile.getItems().add(settingsMenuItem);*/

        binding.topMenuFile.getItems().add(new SeparatorMenuItem());

        final MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(new WeakEventHandler<>(event -> {
            Platform.exit();
        }));
        binding.topMenuFile.getItems().add(exitMenuItem);

        // Menu Help
        final MenuItem aboutMenuItem = new MenuItem("About " + AppInfo.name);
        aboutMenuItem.setOnAction(event -> {
            showAboutDialog();
        });
        binding.topMenuHelp.getItems().add(aboutMenuItem);
    }

    private void closeProject() {
        // do other
        getViewModel().workingProject(null);
    }

    private void showNewProjectWizardDialog() {
        new NewProjectWizardDialog(Plugin.TYPE_MOVIE).showAndWait().ifPresent(project -> {
            WorkingDialog<Integer> workingDialog = new WorkingDialog<>(() -> {
                ProjectManager.initializeProject(project);
                return -1;
            });
            workingDialog.setTitle("Creating...");
            workingDialog.setOnFinishListener((r) -> {
                getViewModel().workingProject(project);
                getViewModel().bottomStatus("");
            });
            workingDialog.show();
            getViewModel().bottomStatus("Creating Project(" + project.getName() + ")...");
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
        HomeStageViewBinding binding = getBinding();
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
            loadProjectTreeView();
        }
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
        File[] files = rootDirectory.listFiles();
        if (files == null) return;

        TreeView<File> dirTreeView = ((TreeView<File>) getBinding().dirTreeView);
        TreeItem<File> rootTreeItem = new TreeItem<>(rootDirectory);

        rootTreeItem.setExpanded(true);
        addTreeViewItem(rootDirectory, rootTreeItem, false);
        dirTreeView.setRoot(rootTreeItem);
    }

    private void addTreeViewItem(File currentFile, TreeItem<File> currentTreeItem, boolean isRecursive) {
        File[] files = currentFile.listFiles();
        if (files == null) return;
        for (File childFile : files) {
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
            hasFileOpened = new SimpleBooleanProperty() {
                @Override
                protected void invalidated() {
                    HomeStageViewBinding binding = getBinding();
                    if (get()) {
                        if (binding.borderPanelContent.getCenter() == null) {
                            binding.borderPanelContent.setCenter(binding.taFileEditor);
                        }
                    } else {
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
        if (file == null || !file.exists()) return;

        WorkingDialog<String> workingDialog = new WorkingDialog<>(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
        });
        workingDialog.setTitle("Opening File " + file.getName());
        workingDialog.show();
        workingDialog.run();
        getViewModel().bottomStatus("Opening File(" + file.getName() + ")...");
    }

    ///////////////////////////////////////////////////////////////////////////
    // The Simple Editor
    ///////////////////////////////////////////////////////////////////////////

    private void initializeEditor() {
        TextArea taFileEditor = getBinding().taFileEditor;
        taFileEditor.setOnKeyReleased(event -> {
            Logger.getLogger(HomeStage.class).info("Event Released: " + event.toString());
            System.out.println("Event Released: " + event.toString());
        });
    }

    @Override
    public void onHidden(WindowEvent event) {
        super.onHidden(event);
        stopWatchProjectDir();
        Optional.ofNullable(viewModel).map(Reference::get).ifPresent(HomeStateViewModel::destroy);
        closeProjectMenuItem = null;
    }
}
