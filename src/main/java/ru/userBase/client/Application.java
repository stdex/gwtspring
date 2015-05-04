package ru.userBase.client;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.user.client.Timer;
import ru.userBase.shared.dto.UserDTO;
import ru.userBase.shared.services.UserServiceAsync;

import java.util.*;

public class Application
        implements EntryPoint {

    private static final String SERVER_ERROR = "An error occurred while attempting to contact the server. Please check your network connection and try again. The error is : ";
    private final UserServiceAsync userService = UserServiceAsync.Util.getInstance();    
    private final List<UserDTO> CONTACTS = new ArrayList();
    

    public void onModuleLoad() {
        final Set<Long> userDel = new HashSet<Long>();
        final CellTable<UserDTO> table = new CellTable();
        final Button saveOrUpdateButton = new Button("Сохранить");
        final Button addUserButton = new Button("Добавить");
        final Button retrieveButton = new Button("Удалить");
        final Button exportBase = new Button("Экспорт");
        final Button closeButton = new Button("OK");
        final Button update = new Button("Обновить");
        final TextBox userNameField = new TextBox();
        final TextBox userPhoneNumberField = new TextBox();
        userNameField.setText("ФИО");
        userPhoneNumberField.setText("Номер телефона");
        final TextBox editUserNameField = new TextBox();
        final TextBox editUserPhoneNumber = new TextBox();
        final Label editUserId = new Label();
        final Button cancelEditUser = new Button("Отмена");
        final TextBox userIdField = new TextBox();
        final Label errorLabel = new Label();
        final DialogBox addUser = new DialogBox();
        final DialogBox editUserBox = new DialogBox();
        final DialogBox dialogBox = new DialogBox();
        final Image image = new Image();
        final Label textToServerLabel = new Label();
        final HTML serverResponseLabel = new HTML();

        // кнопка удаления неактивна
        retrieveButton.setEnabled(false);

        // рисуем картинку
        image.setSize("250", "350");
        image.setUrl(GWT.getModuleBaseURL() + "phone_book.jpg");

        // выводим все элементы на страницу
        RootPanel.get("ImageContainer").add(image);
        RootPanel.get("userIdFieldContainer").add(table);
        
        RootPanel.get("addUserButtonContainer").add(addUserButton);
        RootPanel.get("retrieveUserButtonContainer").add(retrieveButton);
        RootPanel.get("exportButtonContainer").add(exportBase);
        
        RootPanel.get("errorLabelContainer").add(errorLabel);
        
        

        // устанавливаем значения по умолчанию для полей ввода информвции
        userNameField.setFocus(true);
        userNameField.selectAll();
        userPhoneNumberField.selectAll();

        dialogBox.setText("Remote Procedure Call");
        dialogBox.setAnimationEnabled(true);

        closeButton.getElement().setId("closeButton");

        // панель ответа сервера
        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.addStyleName("dialogVPanel");
        dialogVPanel.add(new HTML("<b>Sending request to the server:</b>"));
        dialogVPanel.add(textToServerLabel);
        dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
        dialogVPanel.add(serverResponseLabel);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        dialogVPanel.add(closeButton);
        dialogBox.setWidget(dialogVPanel);

        // внутренний класс для получения списка юзеров
        class FindAll {

            private List<UserDTO> alles() {
                userService.allUser(new AsyncCallback<List<UserDTO>>() {
                    public void onFailure(Throwable caught) {
                        dialogBox.setText("Remote Procedure Call - Failure");
                        serverResponseLabel
                                .addStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML("An error occurred while attempting to contact the server. Please check your network connection and try again. The error is : " + caught.toString());
                        dialogBox.center();
                        closeButton.setFocus(true);
                    }

                    @Override
                    public void onSuccess(List<UserDTO> result) {
                        CONTACTS.clear();
                        CONTACTS.addAll(result);
                    }
                });
                return CONTACTS;
            }

            private Integer count() {
                userService.allUser(new AsyncCallback<List<UserDTO>>() {
                    public void onFailure(Throwable caught) {
                        dialogBox.setText("Remote Procedure Call - Failure");
                        serverResponseLabel
                                .addStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML("An error occurred while attempting to contact the server. Please check your network connection and try again. The error is : " + caught.toString());
                        dialogBox.center();
                        closeButton.setFocus(true);
                    }

                    public void onSuccess(List<UserDTO> result) {
                        CONTACTS.clear();
                        CONTACTS.addAll(result);
                    }
                });
                return Integer.valueOf(CONTACTS.size());
            }
        }
        ;
       final FindAll findAll = new FindAll();


//        рисуем таблицу
        table.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);

        final SelectionModel<UserDTO> selectionModel = new MultiSelectionModel<UserDTO>();
        table.setSelectionModel(selectionModel, DefaultSelectionEventManager.<UserDTO>createCheckboxManager());
        Column<UserDTO, Boolean> checkColumn = new Column<UserDTO, Boolean>(new CheckboxCell(true, false)) {
            public Boolean getValue(UserDTO object) {
                if (selectionModel.isSelected(object) == true)  {
                           userDel.add(object.getUserId());
                    retrieveButton.setEnabled(true);
            }  else {
                     userDel.remove(object.getUserId());
                     if (userDel.size()==0){
                     retrieveButton.setEnabled(false);
                     }
                }
                return selectionModel.isSelected(object);
            }
        };

        checkColumn.setFieldUpdater( new FieldUpdater<UserDTO, Boolean>() {
            @Override
            public void update(int index, UserDTO object, Boolean value) {
            }
        });

        table.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));

        TextColumn<UserDTO> idColumn = new TextColumn<UserDTO>() {
            public String getValue(UserDTO object) {
                return String.valueOf(object.getUserId());
            }
        };
        idColumn.setSortable(true);
        table.addColumn(idColumn, "ID");

        TextColumn<UserDTO> nameColumn = new TextColumn<UserDTO>() {
            public String getValue(UserDTO object) {
                return object.getUserName();
            }
        };
        nameColumn.setSortable(true);
        table.addColumn(nameColumn, "ФИО");
        
        TextColumn<UserDTO> phoneNumberColumn = new TextColumn<UserDTO>() {
            public String getValue(UserDTO object) {
                return object.getPhoneNumber();
            }
        };
        phoneNumberColumn.setSortable(true);
        table.addColumn(phoneNumberColumn, "Номер телефона");

        table.setRowData(0, findAll.alles());
        table.setRowCount(findAll.count(), true);

        table.redraw();
// таймер для перерисовки таблицы после обновления страницы
         Timer t = new Timer() {
             @Override
             public void run() {
                 long userId = 0;
                 for (int i = 0; i < findAll.count(); i++) {
                     if (userId <= (findAll.alles().get(i)).getUserId()) {
                         userId = (findAll.alles().get(i)).getUserId() + 1;
                     }
                 }
                 table.setRowData(0, findAll.alles());
                 table.setRowCount(findAll.count(), true);
                 table.redraw();
             }
         };
         // устанавливае время в милисекундах
         t.schedule(500);

        // кнопка экспорта базы
        exportBase.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.open("../Download",
                        "download", "");
            }
        });

        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                saveOrUpdateButton.setEnabled(true);
                saveOrUpdateButton.setFocus(true);

                table.setRowData(findAll.alles());
                table.setRowCount(findAll.count(), true);

                table.setRowData(0, findAll.alles());
                table.redraw();
            }
        });

        // кнопка закрытия панели редактирования
        cancelEditUser.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                editUserBox.hide();
            }
        });

        // панель редактирования юзера
        VerticalPanel editVerticalPanel = new VerticalPanel();
        editVerticalPanel.add(editUserId);
        editVerticalPanel.add(editUserNameField);
        editVerticalPanel.add(editUserPhoneNumber);
        editVerticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        HorizontalPanel editHorisontalPanel = new HorizontalPanel();
        editHorisontalPanel.add(update);
        editHorisontalPanel.setVerticalAlignment(HorizontalPanel.ALIGN_BOTTOM);
        editHorisontalPanel.add(cancelEditUser);
        editVerticalPanel.add(editHorisontalPanel);
        editUserBox.add(editVerticalPanel);

        // кнопка отмены добавления нового юзера
        Button back = new Button("Отмена");
        back.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                addUser.hide();
            }
        });

        // панель добавления нового юзера
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setSpacing(5);
        verticalPanel.add(userNameField);
        verticalPanel.add(userPhoneNumberField);
        verticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(saveOrUpdateButton);
        horizontalPanel.setVerticalAlignment(HorizontalPanel.ALIGN_BOTTOM);
        horizontalPanel.add(back);
        verticalPanel.add(horizontalPanel);
        addUser.setWidget(verticalPanel);

        addUserButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                addUser.setText("Добавить");
                addUser.center();
                addUser.show();
            }
        });

        // класс для удаления юзера
        class RetrieveUserhandler implements ClickHandler {

            public void onClick(ClickEvent event) {
                for (Long i : userDel) {
                    sendUserIdToServer(i);
                }
            }

            private synchronized void sendUserIdToServer(long id) {
                errorLabel.setText("");

                final String textToServer = userIdField.getText();

                textToServerLabel.setText(String.valueOf(textToServer));
                serverResponseLabel.setText("");


                userService.deleteUser(id, new AsyncCallback<Void>() {
                    public void onFailure(Throwable caught) {
                        dialogBox.setText("Remote Procedure Call - Failure");
                        serverResponseLabel
                                .addStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML("An error occurred while attempting to contact the server. Please check your network connection and try again. The error is : " + caught.toString());
                        dialogBox.center();
                        closeButton.setFocus(true);
                    }

                    public void onSuccess(Void aVoid) {
                        dialogBox.setText("Remote Procedure Call");
                        serverResponseLabel
                                .removeStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML("Delete user" + textToServer);

                        dialogBox.center();
                        closeButton.setFocus(true);
                        userIdField.setText(null);
                        table.setRowData(findAll.alles());
                        table.setRowCount(findAll.count(), true);

                        table.setRowData(0, findAll.alles());
                        table.redraw();
                    }
                });
            }
        }
        ;
        RetrieveUserhandler retrieveUserhandler = new RetrieveUserhandler();
        retrieveButton.addClickHandler(retrieveUserhandler);

        // класс для добавления нового юзера
        class AddUserPanel implements ClickHandler {

            public synchronized void onClick(ClickEvent arg0) {
                errorLabel.setText("");

                addUser.hide();

                long userId = 0;
                for (int i = 0; i < findAll.count(); i++) {
                    if (userId <= (findAll.alles().get(i)).getUserId()) {
                        userId = (findAll.alles().get(i)).getUserId() + 1;
                    }
                }
                String userName = userNameField.getText();
                String userPhoneNumber = userPhoneNumberField.getText();

                textToServerLabel.setText("User Id " + String.valueOf(userId));
                userService.saveOrUpdateUser(userId, userName, userPhoneNumber, new AsyncCallback<Void>() {
                    public void onFailure(Throwable caught) {
                        dialogBox.setText("Remote Procedure Call - Failure");
                        serverResponseLabel
                                .addStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML("An error occurred while attempting to contact the server. Please check your network connection and try again. The error is : " + caught.toString());
                        dialogBox.center();
                        closeButton.setFocus(true);
                    }

                    public void onSuccess(Void noAnswer) {
                        dialogBox.setText("Remote Procedure Call");
                        serverResponseLabel
                                .removeStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML("OK");
                        dialogBox.center();
                        closeButton.setFocus(true);
                        table.setRowData(findAll.alles());
                        table.setRowCount(findAll.count(), true);

                        table.setRowData(0, findAll.alles());
                        table.redraw();
                    }
                });
            }
        }
        ;

        AddUserPanel addUserPanel = new AddUserPanel();
        saveOrUpdateButton.addClickHandler(addUserPanel);
    }
}
