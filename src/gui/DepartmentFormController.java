package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{
	
	// instacia da classe Department
	private Department entity;
	
	// instacia da classe DepartmentService
	private DepartmentService service;
	
	// Cria uma lista para receber os objetos com os eventos para DataChangeListener
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	// Metodos SET
	public void setDepartment (Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	// Metodo para interface DataChangeListener adicionar na lista de eventos
	public void subscribeDataChangelistener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	// Metodos de controles dos items da tela
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		// Os if avisão se as dependencias do entity e do service não forem feitas
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Entity was null");
		}
		try {
			entity = getFormData();  // Pega os dados das caixas de texto da tela
			service.saveOrUpdate(entity);  // Salva no banco de dados
			notifyDataChangeListener(); // Metodo para fazer a notificação da modificação de dados
			Utils.currentStage(event).close();  // Fecha a janela 
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Department getFormData() {
		Department obj = new Department();
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));  // Busca o Id e verifica na classe Utils se e inserção ou update
		obj.setName(txtName.getText());
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();  // Fecha a janela 
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNode();
	}
	
	private void initializeNode() {
		Constraints.setTextFieldInteger(txtId);  // O campo Id so aceita numero inteiro
		Constraints.setTextFieldMaxLength(txtName, 30);  // O campo nome aceita no maximo 30 caracteres
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}

}
