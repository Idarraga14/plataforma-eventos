package co.edu.uniquindio.pgii.plataforma_eventos.infrastructure.adapter;

import co.edu.uniquindio.pgii.plataforma_eventos.domain.model.Compra;

public class SimuladorPagoAdapter implements ProcesadorPago {
    private final SimuladorPagoExterno pagoExterno;

    public SimuladorPagoAdapter() {
        this.pagoExterno = new SimuladorPagoExterno();
    }

    @Override
    public boolean procesarPago(Compra compra, String numeroTarjeta, String cvv) {
        // 1. Traducimos los datos de nuestro dominio al formato que exige el banco
        double monto = compra.getTotal();

        // 2. Llamamos al sistema externo
        String respuestaBanco = pagoExterno.realizarCargo(monto, numeroTarjeta, cvv);

        // 3. Traducimos la respuesta del banco a algo que nuestro sistema entienda (boolean)
        return respuestaBanco.startsWith("APROBADO");
    }
}
