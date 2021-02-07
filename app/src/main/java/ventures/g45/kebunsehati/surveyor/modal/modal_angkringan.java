package ventures.g45.kebunsehati.surveyor.modal;

public class modal_angkringan {
    String nama_penjual, nama_juragan, alamat, koordinat;

    public modal_angkringan(){}

    public modal_angkringan(String nama_penjual, String nama_juragan, String alamat, String koordinat) {
        this.nama_penjual = nama_penjual;
        this.nama_juragan = nama_juragan;
        this.alamat = alamat;
        this.koordinat = koordinat;
    }

    public String getNama_penjual() {
        return nama_penjual;
    }

    public void setNama_penjual(String nama_penjual) {
        this.nama_penjual = nama_penjual;
    }

    public String getNama_juragan() {
        return nama_juragan;
    }

    public void setNama_juragan(String nama_juragan) {
        this.nama_juragan = nama_juragan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(String koordinat) {
        this.koordinat = koordinat;
    }
}
