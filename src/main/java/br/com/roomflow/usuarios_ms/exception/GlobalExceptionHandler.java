package br.com.roomflow.usuarios_ms.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ProblemDetail tratarApiException(ApiException ex) {
        HttpStatus status = statusParaErro(ex.getTipoErro());

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(status, ex.getMessage());

        problem.setTitle("Erro na aplicação");
        problem.setProperty("tipoErro", ex.getTipoErro());
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratarErrosDeValidacao(
            MethodArgumentNotValidException ex) {

        ProblemDetail problem = criarProblem(
                HttpStatus.BAD_REQUEST,
                "Erro de validação",
                "Um ou mais campos possuem valores inválidos."
        );

        List<Map<String, String>> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> Map.of(
                        "campo", erro.getField(),
                        "mensagem", erro.getDefaultMessage()
                ))
                .toList();

        problem.setProperty("erros", erros);

        return problem;
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    public ProblemDetail tratarRequisicaoInvalida(Exception ex) {
        return criarProblem(
                HttpStatus.BAD_REQUEST,
                "Requisição inválida",
                "A requisição contém parâmetros ausentes ou valores inválidos."
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail tratarViolacaoDeIntegridade(
            DataIntegrityViolationException ex) {

        log.warn("Violação de integridade dos dados", ex);

        return criarProblem(
                HttpStatus.CONFLICT,
                "Conflito de dados",
                "A operação não pode ser realizada devido a um conflito com os dados existentes."
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail tratarHttpRequestParaMetodoNaoSuportado(
            HttpRequestMethodNotSupportedException ex) {
        log.warn("Metodo HTTP não suportado para essa rota", ex);

        return criarProblem(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Metodo não suportado",
                "Essa rota não suporta este método http"
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail tratarNoResourceFound(
            NoResourceFoundException ex) {
        log.warn("Recurso não encontrado", ex);

        return criarProblem(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                "Rota inválida ou inexistente."
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail tratarMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex) {
        log.warn("Media Type não suportado", ex);

        return criarProblem(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Media Type não supertada.",
                "A aplicação não suporta este tipo de Media Type."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> tratarExcecaoGenerica(Exception ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            return tratarErrorResponse(errorResponse, ex);
        }

        log.error("Erro inesperado na aplicação", ex);

        ProblemDetail problem = criarProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                "Ocorreu um erro interno ao processar a requisição."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private ResponseEntity<ProblemDetail> tratarErrorResponse(ErrorResponse errorResponse, Exception ex) {
        HttpStatusCode status = errorResponse.getStatusCode();

        if (status.is5xxServerError()) {
            log.error("Erro do servidor: {}", ex.getMessage(), ex);
        } else {
            log.warn("Requisição rejeitada ({}): {}", status.value(), ex.getMessage());
        }

        ProblemDetail problem = errorResponse.getBody();
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity
                .status(status)
                .headers(errorResponse.getHeaders())
                .body(problem);
    }

    private ProblemDetail criarProblem(
            HttpStatus status,
            String titulo,
            String detalhe) {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(status, detalhe);

        problem.setTitle(titulo);
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    private HttpStatus statusParaErro(TipoErro tipoErro) {
        return switch (tipoErro) {
            case VALIDACAO -> HttpStatus.BAD_REQUEST;
            case RECURSO_NAO_ENCONTRADO -> HttpStatus.NOT_FOUND;
            case CONFLITO -> HttpStatus.CONFLICT;
        };
    }
}
