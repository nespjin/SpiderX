# nesp-sdk-javafx

Nesp software development kit for JavaFx

## Features

### Resource

Required [nesp-gradle-plugin-javafx](https://github.com/nespjin/nesp-gradle-plugin-javafx)

#### String

```java
final String title=getResource().getString(R.string.app_name);
        setTitle(title);
```

#### Layout

```java
setContent(R.layout.main_stage);
```

### Database

```java

class UserDatabase extends Database {
    UserDatabase() {
        super("DatabaseName", "Password");
    }

    @Override
    public boolean onUpgrade(int oldVersion, int newVersion) {
        System.out.println("onUpgrade: ");
        // Database migrate sample.
        final DatabaseMigrationManager databaseMigrationManager = new DatabaseMigrationManager
                (database, oldVersion, newVersion);
        databaseMigrationManager.setOnMigrationListener(
                new DatabaseMigrationManager.OnMigrationListener() {
                    @Override
                    public void onBeforeMigration() {
                    }

                    @Override
                    public void onMigration(final AbsDatabaseMigration migration) {
                    }

                    @Override
                    public void onAfterMigration() {
                    }
                });

        databaseMigrationManager.addMigration(new Migration_1_2());
        databaseMigrationManager.addMigration(new Migration_2_3());
        databaseMigrationManager.addMigration(new Migration_3_4());
        databaseMigrationManager.addMigration(new Migration_4_5());
        databaseMigrationManager.migrate();
        return true;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void onCreate() {
        execUpdate(Sql.CREATE_TABLE);
    }

}
```

### ViewBinding
```java
package com.nesp.javafx.sample;

import com.nesp.sdk.java.lang.SingletonFactory;
import com.nesp.sdk.javafx.BaseStage;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

public class MainStage extends BaseStage {

    private static final String TAG = "MainStage";

    private MainStage() {
        //no instance
    }

    private MainStageViewBinding mBinding;

    private static boolean isShown = false;

    public static void showWindow() {
        if (isShown) return;
        var shared =
                SingletonFactory.getWeakInstance(MainStage.class, MainStage::new);
        shared.show();
        isShown = true;
    }

    @Override
    public void onCreate(final @NotNull Stage stage) {
        super.onCreate(stage);
        initializeViews();
    }

    private void initializeViews() {
        mBinding = MainStageViewBinding.inflate(R.layout.main_stage);
        setContent(mBinding.getRoot());
        final String title = getResource().getString(R.string.app_name);
        setTitle(title);

        StringProperty buttonText = new SimpleStringProperty("Click Me");

        IntegerProperty clickCount = new SimpleIntegerProperty() {
            @Override
            protected void invalidated() {
                super.invalidated();
                buttonText.setValue("Clicked " + get());
            }
        };

        mBinding.btn_click.setOnMouseClicked(event -> clickCount.set(clickCount.get() + 1));
        mBinding.btn_click.textProperty().bind(buttonText);

        mBinding.btn_switch_lang.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                if (Objects.equals(Locale.getDefault().getLanguage(), "zh")) {
                    Locale.setDefault(Locale.ENGLISH);
                    mBinding.btn_switch_lang.setText("中文");
                } else {
                    Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
                    mBinding.btn_switch_lang.setText("English");
                }

                recreate();
            }
        });

        if (!Objects.equals(Locale.getDefault().getLanguage(), "zh")) {
            mBinding.btn_switch_lang.setText("中文");
        } else {
            mBinding.btn_switch_lang.setText("English");
        }
    }


}

```

## Dependencies

- [JavaFx 17.0.1](https://openjfx.io/)
- [OpenJDK 17.0.1](http://www.planetjdk.org/projects/jdk/)
- [nesp-sdk-java](https://github.com/nespjin/nesp-sdk-java)

## License

```
Copyright 2019 靳兆鲁（1756404649@qq.com）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```
