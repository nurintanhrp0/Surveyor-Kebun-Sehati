package ventures.g45.kebunsehati.surveyor.modal;

public class modal_invoice {
    String id_invoice, tanggal, due_date, status, id_horeka;
    Integer total;

    public modal_invoice(){}

    public modal_invoice(String id_invoice, String tanggal, String due_date, String status, String id_horeka, Integer total) {
        this.id_invoice = id_invoice;
        this.tanggal = tanggal;
        this.due_date = due_date;
        this.status = status;
        this.id_horeka = id_horeka;
        this.total = total;
    }

    public String getId_invoice() {
        return id_invoice;
    }

    public void setId_invoice(String id_invoice) {
        this.id_invoice = id_invoice;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId_horeka() {
        return id_horeka;
    }

    public void setId_horeka(String id_horeka) {
        this.id_horeka = id_horeka;
    }
}
