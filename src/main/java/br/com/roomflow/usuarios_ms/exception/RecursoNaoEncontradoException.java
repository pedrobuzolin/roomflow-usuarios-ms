package br.com.roomflow.usuarios_ms.exception;

import static br.com.roomflow.usuarios_ms.exception.TipoErro.RECURSO_NAO_ENCONTRADO;

public class RecursoNaoEncontradoException extends ApiException {
    public RecursoNaoEncontradoException(String message) {
        super(message, RECURSO_NAO_ENCONTRADO);
    }
}
