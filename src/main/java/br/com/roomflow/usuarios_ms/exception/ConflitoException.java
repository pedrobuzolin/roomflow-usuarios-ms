package br.com.roomflow.usuarios_ms.exception;

import static br.com.roomflow.usuarios_ms.exception.TipoErro.CONFLITO;

public class ConflitoException extends ApiException {
    public ConflitoException(String message) {
        super(message, CONFLITO);
    }
}
