import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:todoeyflutter/models/task_data.dart';
import 'package:todoeyflutter/widgets/task_tile.dart';

class TaskList extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Consumer<TaskData>(
      builder: (context, taskData, child){
        return ListView.builder(itemBuilder: (context,index){
          final task = taskData.tasks[index];
          return GestureDetector(
            onLongPress: (){
              taskData.deleteTask(index);
            },
            child: TaskTile(
              title:task.name,
              isChecked: task.isDone,
              onChanged: (val){
                taskData.updateTask(task);
              },),
          );
        },
          itemCount: taskData.taskCount,);
      },
    );
  }
}