package com.example.myapplication.cliente.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.cliente.model.adapter.rv.AdaptadorEventosCliente
import com.example.myapplication.data.model.ReservarEvento
import com.example.myapplication.data.model.Usuario
import com.example.myapplication.data.model.UsuarioActual
import com.example.myapplication.databinding.FragmentClienteVerEventoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClienteVerEventoFragment : Fragment() {

    private var _binding: FragmentClienteVerEventoBinding? = null
    private val binding get() = _binding!!

    private lateinit var recycler: RecyclerView
    private lateinit var adaptador: AdaptadorEventosCliente
    @Inject
    lateinit var dbRef: DatabaseReference
    private var idUsuario = ""
    private var lista: MutableList<ReservarEvento> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClienteVerEventoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Codigo
        if(UsuarioActual.usuarioActual != null){
            val usuarioActual: Usuario = UsuarioActual.usuarioActual!!
            idUsuario = usuarioActual.idUsuario
        }
        configurarRecyclerView()
        configurarMenuPopup()
        configurarSearchView()


    }

    private fun configurarRecyclerView(){
        dbRef.child("tienda").child("reservas_eventos").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { evento ->
                    val pojoReservarEvento = evento.getValue(ReservarEvento::class.java)
                    Log.e("Id user dentro de pojo", pojoReservarEvento?.idUsuario.toString())
                    Log.e("Id user", idUsuario)
                    if(pojoReservarEvento?.idUsuario.toString() == idUsuario){
                        if (pojoReservarEvento != null) {
                            lista.add(pojoReservarEvento)
                        }
                    }
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })
        //
        adaptador = AdaptadorEventosCliente(lista)
        apply {
            recycler = binding.rvMostrarEventos
            recycler.adapter = adaptador
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.setHasFixedSize(true)
        }
    }

    private fun configurarSearchView() {
        // Configurar el SearchView
        binding.svBuscaEvento.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptador.filter(newText.orEmpty())
                return true
            }
        })
    }

    private fun configurarMenuPopup() {
        //Boton popup
        binding.ivMenuOpciones.setOnClickListener {
            mostrarPopupMenu(it)
        }
    }

    private fun mostrarPopupMenu(view: View?) {
        // Crear instancia de PopupMenu
        val popupMenu = view?.let { android.widget.PopupMenu(context, it) }

        // Inflar el menú desde el archivo XML
        popupMenu?.menuInflater?.inflate(R.menu.popup_menu_administrador_eventos, popupMenu.menu)

        // Establecer un listener para manejar clics en las opciones del menú
        popupMenu?.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_sort_name -> {
                    // Lógica para la opción "ordenar alfabeticamente"
                    lista.sortBy { evento ->
                        evento.nombre
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_precio -> {
                    // Lógica para la opción "ordenar por precio"
                    lista.sortByDescending { evento ->
                        evento.precio
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_ocupacion -> {
                    // Lógica para la opción "ordenar por ocupacion"
                    lista.sortByDescending { evento ->
                        evento.aforoOcupado
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_aforo -> {
                    // Lógica para la opción "ordenar por aforo"
                    lista.sortByDescending { evento ->
                        evento.aforo
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                R.id.action_sort_fecha -> {
                    // Lógica para la opción "ordenar por fecha"
                    lista.sortByDescending { evento ->
                        evento.fecha
                    }
                    recycler.adapter?.notifyDataSetChanged()
                    true
                }

                else -> false
            }
        }
        // Mostrar el menú emergente
        popupMenu?.show()
    }
}