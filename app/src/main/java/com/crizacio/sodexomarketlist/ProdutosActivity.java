package com.crizacio.sodexomarketlist;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProdutosActivity extends AppCompatActivity {

    ListView lstProductos;
    List<Producto> productos;
    TextView txtTotal;
    Button btnAgregar;
    int pTotal = 0;
    int saldoTotal = -1; // para no hacer comprobacion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//            .build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .penaltyLog()
//                .penaltyDeath()
//                .build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        lstProductos = (ListView) findViewById(R.id.lstProductos);
        productos = new ArrayList<>();

        txtTotal = (TextView)findViewById(R.id.txtTotal);
        btnAgregar = (Button)findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNuevoProductoDialog();
            }
        });
        lstProductos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Producto producto = productos.get(i);
                showUpdateDeleteDialog(i, producto);
                return true;
            }
        });
        txtTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPresupuestoDialog();
            }
        });
    }

    public void IndexarProductos(String mensaje) {
        pTotal = 0;
        if (mensaje != null) {
            Toast.makeText(getApplicationContext(), mensaje,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Cargando...",Toast.LENGTH_SHORT).show();
        }
        for(int i=productos.size()-1; i>-1; i--){
            Producto producto = new Producto(
                    productos.get(i).getNombre(),
                    productos.get(i).getCantidad(),
                    productos.get(i).getPrecio(),
                    productos.get(i).getImagen()
            );
            pTotal += (productos.get(i).getPrecio() * productos.get(i).getCantidad());
        }
        if (saldoTotal > 0) {
            if (pTotal > saldoTotal) { // si la suma de productos es mayor a la cantidad actual en la cuenta
                txtTotal.setBackgroundColor(Color.parseColor("#FF0000"));
            } else { // si la suma de productos es menor a la cantidad actual en la cuenta
                txtTotal.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
        txtTotal.setText("Total: $ "+ pTotal);
        ListaProductos listaProductosAdapter = new ListaProductos(ProdutosActivity.this, productos);
        lstProductos.setAdapter(listaProductosAdapter);
    }

    private void showNuevoProductoDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.newproduct_dialog, null);
        dialogBuilder.setView(dialogView);

        final ImageView _imgProducto = (ImageView) dialogView.findViewById(R.id.imgProducto);
        final EditText _edtNombre = (EditText) dialogView.findViewById(R.id.edtNombre);
        final EditText _edtPrecio = (EditText) dialogView.findViewById(R.id.edtPrecio);
        final EditText _edtCantidad = (EditText) dialogView.findViewById(R.id.edtCantidad);
        final Button _btnAgregar = (Button) dialogView.findViewById(R.id.btnAgregar);
        final Button _btnImagen = (Button) dialogView.findViewById(R.id.btnImagen);

        dialogBuilder.setTitle("Agregar producto");
        final AlertDialog b = dialogBuilder.create();
        b.show();
        final String[] imagen = new String[1];
        imagen[0] = "";
        _btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _nombre = _edtNombre.getText().toString().trim();
                String cantidad_ = _edtCantidad.getText().toString().trim();
                String precio_ = _edtPrecio.getText().toString().trim();
                String _imagen = imagen[0].toString().trim();
                if (!TextUtils.isEmpty(_nombre) && !TextUtils.isEmpty(cantidad_) && !TextUtils.isEmpty(precio_)) {
                    int _cantidad = Integer.parseInt(cantidad_);
                    int _precio = Integer.parseInt(precio_);
                    if (_cantidad > 0 && _precio > 0) {
                        Producto producto = new Producto(_nombre, _cantidad, _precio, _imagen);
                        productos.add(producto);
                        IndexarProductos("Agregado correctamente!");
                        b.dismiss();
                    }
                }
            }
        });
        _btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        imagen[0] = photoFile.getAbsolutePath();
                    } catch (IOException ex) {}
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(ProdutosActivity.this,"com.example.android.fileprovider",photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 1);
                    }
                }
            }
        });
    }
    private void showUpdateDeleteDialog(final int index, Producto producto) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.newproduct_dialog, null);
        dialogBuilder.setView(dialogView);

        final ImageView _imgProducto = (ImageView) dialogView.findViewById(R.id.imgProducto);
        final EditText _edtNombre = (EditText) dialogView.findViewById(R.id.edtNombre);
        final EditText _edtPrecio = (EditText) dialogView.findViewById(R.id.edtPrecio);
        final EditText _edtCantidad = (EditText) dialogView.findViewById(R.id.edtCantidad);
        final Button _btnAgregar = (Button) dialogView.findViewById(R.id.btnAgregar);
        final Button _btnImagen = (Button) dialogView.findViewById(R.id.btnImagen);
        final Button _btnQuitar = (Button) dialogView.findViewById(R.id.btnEliminar);

        _btnQuitar.setVisibility(View.VISIBLE);
        _edtNombre.setText(producto.getNombre());
        _edtPrecio.setText(""+producto.getPrecio());
        _edtCantidad.setText(""+producto.getCantidad());
        _btnAgregar.setText("Modificar");

        dialogBuilder.setTitle("Editar producto");
        final AlertDialog b = dialogBuilder.create();
        b.show();
        final String[] imagen = new String[1];
        imagen[0] = producto.getImagen();
        if (!TextUtils.isEmpty(imagen[0])) {
            _imgProducto.setVisibility(View.VISIBLE);
            Picasso.with(ProdutosActivity.this).load(new File(imagen[0])).into(_imgProducto);
        }
        _btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _nombre = _edtNombre.getText().toString().trim();
                String cantidad_ = _edtCantidad.getText().toString().trim();
                String precio_ = _edtPrecio.getText().toString().trim();
                String _imagen = imagen[0].toString().trim();
                if (!TextUtils.isEmpty(_nombre) && !TextUtils.isEmpty(cantidad_) && !TextUtils.isEmpty(precio_)) {
                    int _cantidad = Integer.parseInt(cantidad_);
                    int _precio = Integer.parseInt(precio_);
                    if (_cantidad > 0 && _precio > 0) {
                        Producto producto = new Producto(_nombre, _cantidad, _precio, _imagen);
                        productos.set(index, producto);
                        IndexarProductos("Modificado correctamente!");
                        b.dismiss();
                    }
                }
            }
        });
        _btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        imagen[0] = photoFile.getAbsolutePath();
                    } catch (IOException ex) {}
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(ProdutosActivity.this,"com.example.android.fileprovider",photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 1);
                    }
                }
            }
        });
        _btnQuitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productos.remove(index);
                IndexarProductos("Eliminado correctamente!");
                b.dismiss();
            }
        });
    }
    private void showPresupuestoDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.newproduct_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText _edtNombre = (EditText) dialogView.findViewById(R.id.edtNombre);
        final EditText _edtPrecio = (EditText) dialogView.findViewById(R.id.edtPrecio);
        final EditText _edtCantidad = (EditText) dialogView.findViewById(R.id.edtCantidad);
        final Button _btnAgregar = (Button) dialogView.findViewById(R.id.btnAgregar);
        final Button _btnImagen = (Button) dialogView.findViewById(R.id.btnImagen);
        final Button _btnQuitar = (Button) dialogView.findViewById(R.id.btnEliminar);

        _edtNombre.setVisibility(View.GONE);
        _edtCantidad.setVisibility(View.GONE);
        _btnImagen.setVisibility(View.GONE);
        _btnQuitar.setVisibility(View.GONE);
        _edtPrecio.setHint("Saldo actual");
        if (saldoTotal > 0) {
            _edtPrecio.setText(""+saldoTotal);
        }
        dialogBuilder.setTitle("Agregar saldo actual");
        final AlertDialog b = dialogBuilder.create();
        b.show();
        _btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String saldo = _edtPrecio.getText().toString().trim();
                if (!TextUtils.isEmpty(saldo)) { // si no esta vacio
                    int _precio = Integer.parseInt(saldo);
                    if (_precio < 0) { // si es negativo
                        saldoTotal = -1;
                    } else { // si es positivo
                        saldoTotal = _precio;
                    }
                } else { // si esta vacio
                    saldoTotal = -1;
                }
                b.dismiss();
            }
        });
    }
    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("ddMMyyy_HHmmss").format(new Date());
        String imageFileName = "PRODUCTO_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }
}
/* ME AYUDE DE:
*   https://stackoverflow.com/questions/12931798/android-app-multiply-and-divide-the-user-entered-values-values-in-the-two-form
*   https://stackoverflow.com/questions/15523966/add-item-in-array-list-of-android
*   https://stackoverflow.com/questions/2709253/converting-a-string-to-an-integer-on-android
*   https://stackoverflow.com/questions/23681177/picasso-load-image-from-filesystem
*   https://stackoverflow.com/questions/28594297/get-the-directory-from-a-file-path-in-java-android
*   https://thoughtbot.com/blog/android-imageview-scaletype-a-visual-guide
*   ControladorPC & IE2 InstaFeria & eva2_iot_app2 proyects
* */