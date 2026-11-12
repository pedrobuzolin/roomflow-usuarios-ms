package br.com.roomflow.usuarios_ms.exception;

public abstract class ApiException extends RuntimeException {
    private final TipoErro tipoErro;

    protected ApiException(String message, TipoErro tipoErro) {
        super(message);
        this.tipoErro = tipoErro;
    }

    public TipoErro getTipoErro() {
        return tipoErro;
    }
}
