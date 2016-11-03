//
//  VoidDurationListTBC.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 3/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

class VoidDurationListTBC: UITableViewCell {

    //UI Elements
    @IBOutlet weak var label_begin: UILabel!
    @IBOutlet weak var label_name: UILabel!
    @IBOutlet weak var label_duration: UILabel!

    //Functions
    //Data Functions
    /*
     * public func updateData(name:Stirng,begin:NSDate,duration:Int)
     * - Updates the cell for the specified data
     * [Argument]
     * name - Name of the void duration
     * begin - Begin date/time of the void duration
     * duration - Duration of the void duration
    */
    public func updateData(name:String,begin:NSDate,duration:Int)
    {
        //Init Date Data
        let date = Date()
        let cal = Calendar.autoupdatingCurrent
        var begin_dcmp = cal.dateComponents([Calendar.Component.day, Calendar.Component.month,Calendar.Component.year,Calendar.Component.minute,Calendar.Component.hour], from: begin as Date)
        var diff_dcmp = cal.dateComponents([Calendar.Component.day], from: date, to: begin as Date )

        //Name Label
        self.label_name.text = name

        //Begin Label 
        switch diff_dcmp.day! {
        case 0:
            label_begin.text = "\(begin_dcmp.hour) \(begin_dcmp.minute)"
        case 1:
            label_begin.text = "Tommorow,\(begin_dcmp.hour) \(begin_dcmp.minute)"
        default:
            label_begin.text = "\(begin_dcmp.day)/\(begin_dcmp.month)/\(begin_dcmp.year), \(begin_dcmp.hour) \(begin_dcmp.minute)"
        }

        //Duration Label
        var drsn_hour = 0
        var drsn_min = 0
        var drsn_left = duration

        drsn_hour = drsn_left / 60 * 60 * 60
        drsn_left -= drsn_hour * 60 * 60 * 60
        drsn_min = duration / 60 * 60

        self.label_duration.text = "\(drsn_hour)h \(drsn_min)m"
    }
    //Event Functions
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    //UI Event Functions
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }

}
