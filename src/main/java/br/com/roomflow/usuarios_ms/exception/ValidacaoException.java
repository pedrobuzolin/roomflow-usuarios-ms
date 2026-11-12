package br.com.roomflow.usuarios_ms.exception;

import static br.com.roomflow.usuarios_ms.exception.TipoErro.VALIDACAO;

public class ValidacaoException extends ApiException {
    public ValidacaoException(String message) {
        super(message, VALIDACAO);
    }
}
