package db

import java.time.LocalDate
import java.util.UUID

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec
import com.amazonaws.services.dynamodbv2.document.{AttributeUpdate, DynamoDB, Item}
import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.services.dynamodbv2.util.Tables
import models.{CarAdvert, Fuel}
import play.api.Play

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

class CarAdvertDb {

  import play.api.Play.current

  private val TABLE: String = "CarAdvert"

  private val client = new AmazonDynamoDBClient()
  client.setEndpoint(Play.configuration.getString("dynamodb.endpoint").getOrElse("http://localhost:8000"))

  private val db = new DynamoDB(client)

  val table = if (!Tables.doesTableExist(client, TABLE)) db.createTable(TABLE,
    Seq(new KeySchemaElement("id", KeyType.HASH)),
    Seq(new AttributeDefinition("id", ScalarAttributeType.S)),
    new ProvisionedThroughput(10l, 10l)
  )
  else db.getTable(TABLE)

  def createAdvert(a: CarAdvert) = {
    val id = UUID.randomUUID()
    val item = new Item()
      .withPrimaryKey("id", id.toString)
      .withString("title", a.title)
      .withString("fuel", a.fuel.toString)
      .withInt("price", a.price)
      .withBoolean("new", a.`new`)
    a.mileage.fold(item.withNull("mileage"))(item.withInt("mileage", _))
    a.firstRegistration.fold(item.withNull("firstRegistration"))(r => item.withString("firstRegistration", r.toString))
    table.putItem(item)
    a.copy(id = Some(id))
  }

  def modifyAdvert(id: UUID, a: CarAdvert) = {
    val mileageUpdate = new AttributeUpdate("mileage")
    val registrationUpdate = new AttributeUpdate("firstRegistration")

    a.mileage.fold(mileageUpdate.delete())(mileageUpdate.put(_))
    a.firstRegistration.fold(registrationUpdate.delete())(registrationUpdate.put(_))

    table.updateItem(new UpdateItemSpec()
      .withPrimaryKey("id", id.toString)
      .withAttributeUpdate(ListBuffer(
      new AttributeUpdate("title").put(a.title),
      new AttributeUpdate("fuel").put(a.fuel.toString),
      new AttributeUpdate("price").put(a.price),
      new AttributeUpdate("new").put(a.`new`),
      mileageUpdate,
      registrationUpdate
    ))).getItem
  }

  def deleteAdvert(id: UUID) = table.deleteItem("id", id.toString)

  def getAdvert(id: UUID) = table.query("id", id.toString).headOption.map(advertMapper)

  def getAdverts = table.scan().map(advertMapper).toSeq

  private def advertMapper(item: Item): CarAdvert = {
    val id = Some(UUID.fromString(item.getString("id")))
    val title = item.getString("title")
    val fuel = Fuel.withName(item.getString("fuel"))
    val price = item.getInt("price")
    val `new` = item.getBoolean("new")
    val mileage = if (`new`) None else Some(item.getInt("mileage"))
    val firstRegistration = if (`new`) None else Some(LocalDate.parse(item.getString("firstRegistration")))
    CarAdvert(id, title, fuel, price, `new`, mileage, firstRegistration)
  }
}
