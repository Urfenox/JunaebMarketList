package com.crizacio.sodexomarketlist;

import android.app.Activity;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ListaProductos extends ArrayAdapter<Producto> {
    private Activity context;
    List<Producto> producto;
    public ListaProductos(Activity context, List<Producto> producto) {
        super(context, R.layout.producto, producto);
        this.context = context;
        this.producto = producto;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.producto, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.txtNombre);
        TextView textViewQuantity= (TextView) listViewItem.findViewById(R.id.txtCantidadUnitario);
        TextView textViewTotal= (TextView) listViewItem.findViewById(R.id.txtPrecio);
        ImageView imageViewImagen= (ImageView) listViewItem.findViewById(R.id.imgProducto);
        Producto prod = producto.get(position);
        textViewName.setText(prod.getNombre());
        textViewQuantity.setText("Cantidad: " + prod.getCantidad() + " | Unitario: $ " + prod.getPrecio()); // Cantidad: 0 | Unitario: $ precio
        textViewTotal.setText("Total: $ " + prod.getPrecio() * prod.getCantidad()); // Total: $ <precio*cantidad>
        String imagen = prod.getImagen();
        if (!TextUtils.isEmpty(imagen)) {
            Picasso.with(context).load(new File(imagen)).into(imageViewImagen);
        }
        return listViewItem;
    }
}