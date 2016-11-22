//
//  ScheduleListVC.swift
//  Skeem
//
//  Created by Zhu Zhan Yan on 2/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

class ScheduleListVC: UITableViewController {
    //Links
    var DBC:SKMDataController!
    var SCH:SKMScheduler!

    //Data
    var arr_voidd:[SKMVoidDuration]!
    var arr_drsn:[SKMDuration]!
    var dict_schd:[SKMDuration:[SKMTask]]!

    //Status
    var date:Date! //Current Virtual Date

    //UI Elements
    @IBOutlet weak var bbtn_add: UIBarButtonItem!

    //Data Functions
    /*
     * public func updateData()
     * - Updates data to reflect database
     */
    public func updateData()
    {
        //Prepare Data
        self.DBC.updateTask()
        self.DBC.updateVoidDuration()
        self.arr_voidd = self.DBC.sortedVoidDuration(sattr: SKMVoidDurationSort.begin)

        //Date
        self.date = Date() //Init to current date/time

        //Schedule Duration
        /*
        self.arr_drsn = self.SCH.scheduleDuration()

        //Schedule Tasks
        do
        {
            dict_schd = try self.SCH.scheduleTask()
        }
        catch SKMSchedulerError.InsufficentData
        {
            //TODO: Warn user of insufficent data
        }
        catch SKMSchedulerError.DeadlineOverflow
        {
            //TODO: Warn usr of deadline overflow
        }
        catch
        {
            //Unhandled Error
            abort()
        }
 */
    }

    //UI Functions
    /*
     * public func updateUI()
     * - Triggers UI update to reflect current data
    */
    public func updateUI()
    {
        //Update Table View
        self.tableView.reloadData()
    }

    //Events
    override func viewDidLoad() {
        super.viewDidLoad()

        //Load Links
        let appdelegate = (UIApplication.shared.delegate as! AppDelegate)
        self.DBC = appdelegate.DBC
        self.SCH = appdelegate.SCH

        //Update Data
        self.updateData()

        self.updateUI()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)

        self.updateData()
        self.updateUI()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int
    {
        //Main Section
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int
    {
        //Compute total Cells
        /*var cell_cnt = self.arr_voidd.count
        for arr_stsk in self.dict_schd.values
        {
            cell_cnt += arr_stsk.count
        }

        return cell_cnt
        */

        return 0
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        //Prepare Data
        switch indexPath.section {
        case 0: //Main Section
            //Determine if cell is Subtask OR Void Duration
            var loc = 0
            for drsn in self.arr_drsn
            {
                if let voidd = (drsn as? SKMVoidDuration)
                {
                    //Void Duration
                    if loc == indexPath.row
                    {
                        let cell = (self.tableView.dequeueReusableCell(withIdentifier: "uitcell.schdule.voidd" , for: indexPath) as! ScheduleVoidDurationTBC)
                        cell.updateUI(name: voidd.name, subject: "Voidd", begin: voidd.begin as Date, duration: voidd.duration + 1) //NOTE: Adjustment for accurate representation

                        //Update Current Virtual Date
                        let duration = voidd.duration + 1
                        self.date = Date(timeInterval:  TimeInterval(duration), since: voidd.begin as Date)

                        return cell
                    }
                    else
                    {
                        loc += 1
                    }
                }
                else
                {
                    //Schedulable Duration
                    if loc <= indexPath.row && (loc + self.dict_schd[drsn]!.count) >= indexPath.row
                    {
                        let stsk_idx = indexPath.row - loc
                        let stsk = self.dict_schd[drsn]![stsk_idx]

                        let cell = (self.tableView.dequeueReusableCell(withIdentifier: "uitcell.schdule.task", for: indexPath) as! ScheduleTaskTBC)
                        cell.updateUI(name: stsk.name , subject: stsk.subject, description: stsk.descript, duration: stsk.duration, begin: self.date, completion: stsk.completion)

                        //Update Current Virtual Time
                        self.date = Date(timeInterval: TimeInterval(stsk.duration), since: date as Date)

                        return cell
                    }
                    else
                    {
                        loc += self.dict_schd[drsn]!.count
                    }
                }
            }

            abort() //Data Inconsistentcy: Data expected is not present
        default:
            abort()
        }
    }

    @IBAction func bbtn_add_action(_ sender: UIBarButtonItem) {
        //Display Action Sheet of Add Options
        let artsht = UIAlertController(title: "Create New", message: "", preferredStyle: UIAlertControllerStyle.actionSheet)
        artsht.addAction(UIAlertAction(title: "Task", style: UIAlertActionStyle.default, handler: {(artact:UIAlertAction) in
            self.performSegue(withIdentifier: "uisge.task.add", sender: self)
        }))
        artsht.addAction(UIAlertAction(title: "Void Duration", style: UIAlertActionStyle.default, handler: {(artact:UIAlertAction) in
            self.performSegue(withIdentifier: "uisge.voidd.add", sender: self)
        }))
        artsht.addAction(UIAlertAction(title: "Cancel", style: UIAlertActionStyle.cancel, handler: {(artact:UIAlertAction) in
            self.dismiss(animated: true, completion: nil)
        }))

        self.present(artsht, animated: true, completion: nil)
    }

    @IBAction func bbtn_edit_action(_ sender: UIBarButtonItem) {
        if sender.title! == "Edit"
        {
            self.setEditing(true, animated: true)
        }
        else
        {
            self.setEditing(false, animated: true)
        }
    }


    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let sge_idf = segue.identifier
        {
            switch sge_idf {
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
