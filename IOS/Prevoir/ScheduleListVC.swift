//
//  ScheduleListVC.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 2/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

class ScheduleListVC: UITableViewController {
    //Links
    var DBC:PVRDataController!
    var SCH:PVRScheduler!

    //Data
    var arr_voidd:[PVRVoidDuration]!
    var arr_drsn:[PVRDuration]!
    var dict_schd:[PVRDuration:[PVRTask]]!

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
        self.arr_voidd = self.DBC.sortedVoidDuration(sattr: PVRVoidDurationSort.begin)

        //Date
        self.date = Date() //Init to current date/time

        //Schedule Duration
        self.arr_drsn = self.SCH.scheduleDuration()

        //Schedule Tasks
        do
        {
            dict_schd = try self.SCH.scheduleTask()
        }
        catch PVRSchedulerError.InsufficentData
        {
            //TODO: Warn user of insufficent data
        }
        catch PVRSchedulerError.DeadlineOverflow
        {
            //TODO: Warn usr of deadline overflow
        }
        catch
        {
            //Unhandled Error
            abort()
        }
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
        var cell_cnt = self.arr_voidd.count
        for arr_stsk in self.dict_schd.values
        {
            cell_cnt += arr_stsk.count
        }

        return cell_cnt
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
                if let voidd = (drsn as? PVRVoidDuration)
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
                        self.date = Date(timeInterval: TimeInterval(stsk.duration), since: voidd.begin as Date)

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

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
}
