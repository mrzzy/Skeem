//
//  TaskListVC.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 2/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

class TaskListVC: UITableViewController
{
    //Link
    var DBC:PVRDataController!

    //Data
    var arr_task:Array<PVRTask>!

    //Data Functions
    /*
     * public func updateData()
     * - Updates data to reflect database
    */
    public func updateData()
    {
        //Update Data
        self.DBC.updateTask()
        self.arr_task = self.DBC.sortedTask(sattr: PVRTaskSort.deadline)
    }

    //UI Functions
    /*
     * public func updateUI()
     * - Triggers UI update to reflect current data
    */
    public func updateUI()
    {
        //Relead UI Data
        self.tableView.reloadData()
    }

    //Event Functions
    override func viewDidLoad() {
        self.DBC = (UIApplication.shared.delegate as! AppDelegate).DBC
        self.arr_task = Array<PVRTask>()
        self.updateData()

        //Reload UI Data
        super.viewDidLoad()
    }

    override func viewWillAppear(_ animated: Bool) {
        self.updateData()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        let section = 1 //Only one section
        return section
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch section {
        case 0:
            //First Section
            return self.arr_task.count
        default:
            abort() //Unhanded Section
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if self.arr_task.count > 0
        {
            //Create/Update Table View Cell
            let cell = (tableView.dequeueReusableCell(withIdentifier: "uitcell.list.task", for: indexPath) as! TaskListTBC)
            let task = self.arr_task[indexPath.row]
            cell.updateData(name: task.name, subject: task.subject ,completion: task.completion)
            return cell
        }
        else
        {
            //Empty Void Duration Cell
            let cell = tableView.dequeueReusableCell(withIdentifier: "uitcell.list.task.null")!
            return cell
        }
    }

    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        if self.arr_task.count <= 0
        {
            //Cannot Edit Null cell
            return false
        }
        else
        {
            return true
        }
    }

    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == UITableViewCellEditingStyle.delete {
            //Delete From Database
            switch indexPath.section {
            case 0:
                //First Section
                let task = self.arr_task[indexPath.row]
                let rst = self.DBC.deleteTask(name: task.name)
                assert(rst) //Terminates Executable if delete fails
            default:
                abort() //Unhandled Section

            }

            self.updateData()

            //Delete From UI
            tableView.deleteRows(at: [indexPath], with: UITableViewRowAnimation.fade)
        }
        else if editingStyle == UITableViewCellEditingStyle.insert {
            //Data Should be in database
            tableView.insertRows(at: [indexPath], with: UITableViewRowAnimation.automatic)
        }
    }

    // MARK: - Navigation

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let sge_idf = segue.identifier
        {
            switch sge_idf {
            case "uisge.task.add":
                //Add Segue
                let taskeditvc = (segue.destination as! TaskEditVC)
                let _ = taskeditvc.view //Force View Load
                taskeditvc.loadAddTask()
            case "uisge.task.edit":
                //Edit Segue
                let taskeditvc = (segue.destination as! TaskEditVC)
                let _ = taskeditvc.view //Force View Load
                let task =  self.arr_task[self.tableView.indexPathForSelectedRow!.row]
                taskeditvc.loadEditTask(task: task)
            default:
                //Unhandled Segue 
                abort()
            }
        }
        else
        {
            abort()
        }
    }

    @IBAction func unwind_taskListVC(sge:UIStoryboardSegue)
    {
        if let sge_idf = sge.identifier
        {
            switch sge_idf {
            case "uisge.task.list.unwind":
                //Update UI for current data
                self.updateData()
                self.updateUI()
                
            default:
                abort()
            }
        }
        else
        {
            abort()
        }
    }

}
