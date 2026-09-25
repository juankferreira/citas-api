package co.com.fcv.training.citas.application;

public class InsurancePlanNotFound extends RuntimeException {
    public InsurancePlanNotFound() {
        super("Plan de afiliación no disponible");
    }
}
