package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable{
	
	// instacia da classe Seller
	private Seller entity;
	
	// instacia da classe SellerService
	private SellerService service;
	
	// Cria uma lista para receber os objetos com os eventos para DataChangeListener
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	// Metodos SET
	public void setSeller (Seller entity) {
		this.entity = entity;
	}
	
	public void setSellerService(SellerService service) {
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
		catch (ValidationException e) {  // Trata o erro de preechimento do campo name
			setErrorMessages(e.getErros());
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
	
	// Metodo GET
	private Seller getFormData() {
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));  // Busca o Id e verifica na classe Utils se e inserção ou update
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {  // verifica se o campo nome esta vaizio se estiver lança a msg
			exception.addError("name", "Field can´t be empty");
		}
		
		obj.setName(txtName.getText());
		
		if (exception.getErros().size() > 0) {   // Testa se exite um erro e lança a exceção
			throw exception;
		}
		
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
		Constraints.setTextFieldMaxLength(txtName, 70);  // O campo nome aceita no maximo 70 caracteres
		Constraints.setTextFieldDouble(txtBaseSalary);  // O campo Base Salary so aceita Double
		Constraints.setTextFieldMaxLength(txtEmail, 60);  // O campo email aceita no maximo 60 caracteres
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");  // Formata o campo Birth Date
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));  // Converte a data em LocalDate e pega a data defalt do systema
		}
	}
	
	// função SET para preenchimento dos errors na caixa de texto na tela
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}

}
