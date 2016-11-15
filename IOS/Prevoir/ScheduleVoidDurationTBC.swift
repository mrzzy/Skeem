//
//  ScheduleVoidDurationTBC.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 11/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

class ScheduleVoidDurationTBC: UITableViewCell {
    //UI Elements
    @IBOutlet weak var label_begin: UILabel!
    @IBOutlet weak var label_subject: UILabel!
    @IBOutlet weak var label_duration: UILabel!
    @IBOutlet weak var label_name: UILabel!

    //Data Functions
    /*
     * public func updateUI(name:String,subject:String,begin:Date,duration:Int)
     * - Updates UI for data specified
     * [Arguments]
     * name - name of the void duration
     * subject - subject of the void duration
     * begin - begin date/time of the void duration 
     * duration - duration of the void duratio
    */
    public func updateUI(name:String,subject:String,begin:Date,duration:Int)
    {
        //Init Date Data
        let date = Date()
        let cal = Calendar.autoupdatingCurrent
        var begin_dcmp = cal.dateComponents([Calendar.Component.day, Calendar.Component.month,Calendar.Component.year,Calendar.Component.minute,Calendar.Component.hour], from: begin as Date)
        var diff_dcmp = cal.dateComponents([Calendar.Component.day], from: date, to: begin as Date )

        //Name Label
        self.label_name.text = name

        //Begin Label
        //Adjust Label for Readablity
        switch diff_dcmp.day! {
        case 0:
            label_begin.text = "\(begin_dcmp.hour!) \((begin_dcmp.minute! < 10) ? "0\(begin_dcmp.minute!)":  "\(begin_dcmp.minute!)")"
        case 1:
            label_begin.text = "Tommorow,\(begin_dcmp.hour!) \((begin_dcmp.minute! < 10) ? "0\(begin_dcmp.minute!)":  "\(begin_dcmp.minute!)")"
        default:
            label_begin.text = "\(begin_dcmp.day!)/\(begin_dcmp.month!)/\(begin_dcmp.year!), \(begin_dcmp.hour!) \((begin_dcmp.minute! < 10) ? "0\(begin_dcmp.minute!)":  "\(begin_dcmp.minute!)")"
        }

        //Duration Label
        var drsn_hour = 0
        var drsn_min = 0
        var drsn_left = duration

        drsn_hour = drsn_left / (60 * 60)
        drsn_left -= drsn_hour * (60 * 60)
        drsn_min = drsn_left / (60)

        self.label_duration.text = "\(drsn_hour)h \(drsn_min)m"
    }

    //Event Functions
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
