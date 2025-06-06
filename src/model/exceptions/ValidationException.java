package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	// Quarda o erro de cada campo na tela usando pares. primeira String quarda a identificação do campo e a segunda o erro.
	private Map<String, String> erros = new HashMap<>();
	
	public ValidationException(String msg) {
		super(msg);
	}
	
	// Metodo GET
	public Map<String, String>	getErros() {
		return erros;
	}
	
	// Metodo para adicionar o erro a coleção acima
	public void addError(String fieldName, String errorMessage) {
		erros.put(fieldName, errorMessage);
	}
}
