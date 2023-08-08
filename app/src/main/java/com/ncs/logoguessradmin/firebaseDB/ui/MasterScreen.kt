package com.ncs.logoguessradmin.firebaseDB.ui
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ncs.logoguessradmin.firebaseDB.RealTimeModelResponse
import com.ncs.logoguessradmin.utils.ResultState
import com.ncs.logoguessradmin.utils.showMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterScreen(isInsert: MutableState<Boolean>,
                   childName:String,
                   viewModel: MasterViewModel = hiltViewModel()) {

    val scope= rememberCoroutineScope()
    val context= LocalContext.current
    val isDialog= remember {
        mutableStateOf(false)
    }
    val option1= remember {
        mutableStateOf("")
    }
    val option2= remember {
        mutableStateOf("")
    }
    val option3= remember {
        mutableStateOf("")
    }
    val option4= remember {
        mutableStateOf("")
    }
    val answer= remember {
        mutableStateOf("")
    }
    val res=viewModel.res.value
    val isUpdate = remember {
        mutableStateOf(false)
    }

    if (isInsert.value){
        AlertDialog(onDismissRequest = { isInsert.value=false }, confirmButton = {
            Button(onClick = { scope.launch(Dispatchers.Main) {
                viewModel.insert(RealTimeModelResponse.RealTimeItems
                    (listOf(option1.value,option2.value,option3.value,option4.value),
                    answer.value),childName).collect{
                    when(it){
                        is ResultState.Success->{
                            context.showMsg(
                                msg=it.data
                            )
                            isDialog.value=false
                            isInsert.value=false
                        }
                        is ResultState.Failure->{
                            context.showMsg(
                                msg=it.msg.toString()
                            )
                            isDialog.value=false
                        }
                        ResultState.Loading->{
                            isDialog.value=true
                        }
                    }
                }
            }}) {
                Text(text = "Save")
            }
        }, text = {
            Column {
                TextField(value = option1.value, onValueChange ={option1.value=it}, label = { Text(
                    text = "Option 1"
                )} )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(value = option2.value, onValueChange ={option2.value=it}, label = { Text(
                    text = "Option 2"
                )} )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(value = option3.value, onValueChange ={option3.value=it}, label = { Text(
                    text = "Option 3"
                )} )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(value = option4.value, onValueChange ={option4.value=it}, label = { Text(
                    text = "Option 4"
                )} )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(value = answer.value, onValueChange ={answer.value=it}, label = { Text(
                    text = "Answer"
                )} )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        )
    }
    if (isUpdate.value){
        updateMaster(isUpdate = isUpdate, itemState = viewModel.updateRes.value , viewModel = viewModel,childName)
    }

    if(res.item.isNotEmpty()){
        LazyColumn{
            items(res.item, key = {
                it.key!!
            }){res->
                EachRowMaster(itemState = res.item!!, onUpdate = {
                    isUpdate.value=true
                    viewModel.setData(res)
                }){
                    scope.launch(Dispatchers.Main) {
                        res.key?.let {
                            viewModel.delete(it,childName).collect {
                                when (it) {
                                    is ResultState.Success -> {
                                        context.showMsg(
                                            msg = it.data
                                        )
                                    }
                                    is ResultState.Failure -> {
                                        context.showMsg(
                                            msg = it.msg.toString()
                                        )
                                    }

                                    ResultState.Loading -> {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if(res.isLoading){
        Box (modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center ){
            CircularProgressIndicator()
        }
    }
    if(res.error.isNotEmpty()){
        Box (modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center ){
            Text(text = res.error)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun updateMaster(
    isUpdate:MutableState<Boolean>,
    itemState: RealTimeModelResponse,
    viewModel: MasterViewModel,
    childName: String

){
    val option1= remember {
        mutableStateOf(itemState.item?.options?.get(0))
    }
    val option2= remember {
        mutableStateOf(itemState.item?.options?.get(1))
    }
    val option3= remember {
        mutableStateOf(itemState.item?.options?.get(2))
    }
    val option4= remember {
        mutableStateOf(itemState.item?.options?.get(3))
    }
    val answer= remember {
        mutableStateOf(itemState.item?.answer)
    }
    val scope= rememberCoroutineScope()
    val context= LocalContext.current
    if(isUpdate.value){
        AlertDialog(onDismissRequest = { isUpdate.value=false }, confirmButton = {
            Button(onClick = { scope.launch(Dispatchers.Main) {
                viewModel.update(
                    RealTimeModelResponse(item = RealTimeModelResponse.RealTimeItems(
                        listOf(option1.value,option2.value,option3.value,option4.value),
                        answer.value
                    ),key = itemState.key),childName
                ).collect{
                    when(it){
                        is ResultState.Success->{
                            context.showMsg(
                                msg=it.data
                            )
                            isUpdate.value=false

                        }
                        is ResultState.Failure->{
                            context.showMsg(
                                msg=it.msg.toString()
                            )
                        }
                        ResultState.Loading->{
                        }
                    }
                }
            }}) {
                Text(text = "Update")
            }
        }, text = {
            Column {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    Text(text = "Update", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))
                option1.value?.let {
                    TextField(value = it, onValueChange ={option1.value=it}, label = { Text(
                        text = "Option 1"
                    )} )
                }
                Spacer(modifier = Modifier.height(10.dp))
                option2.value?.let {
                    TextField(value = it, onValueChange ={option2.value=it}, label = { Text(
                        text = "Option 2"
                    )} )
                }
                Spacer(modifier = Modifier.height(10.dp))
                option3.value?.let {
                    TextField(value = it, onValueChange ={option3.value=it}, label = { Text(
                        text = "Option 3"
                    )} )
                }
                Spacer(modifier = Modifier.height(10.dp))
                option4.value?.let {
                    TextField(value = it, onValueChange ={option4.value=it}, label = { Text(
                        text = "Option 4"
                    )} )
                }
                Spacer(modifier = Modifier.height(10.dp))
                answer.value?.let {
                    TextField(value = it, onValueChange ={answer.value=it}, label = { Text(
                        text = "Answer"
                    )} )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        )
    }

}
@Composable
fun EachRowMaster( itemState: RealTimeModelResponse.RealTimeItems,
                     onUpdate:()->Unit,
                     onDelete:()->Unit,){
    Box (modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onUpdate()
        }){
        Column (modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)){
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Column {
                    itemState.options?.get(0)?.let { Text(text = "1: ${it}") }
                    itemState.options?.get(1)?.let { Text(text = "2: ${it}") }
                    itemState.options?.get(2)?.let { Text(text = "3: ${it}") }
                    itemState.options?.get(3)?.let { Text(text = "4: ${it}") }
                    Text(text = "Answer : ${itemState.answer}")
                }
                IconButton(onClick = { onDelete() }) {
                    Icon(Icons.Default.Delete, contentDescription = "", tint = Color.Red)
                }
            }
        }
    }
}
