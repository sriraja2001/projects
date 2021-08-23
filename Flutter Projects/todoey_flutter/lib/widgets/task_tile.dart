import 'package:flutter/material.dart';

class TaskTile extends StatelessWidget {
  final isChecked;
  final String title;
  final Function onChanged;
  TaskTile({this.isChecked, this.title, this.onChanged});

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(
        title,
        style: TextStyle(
          decoration: isChecked ? TextDecoration.lineThrough : null,
        ),
      ),
      trailing: Checkbox(
        activeColor: Colors.lightBlueAccent,
        value: isChecked,
        onChanged: onChanged,
//        onChanged: toggleCheckboxState,
      ),
    );
  }
}
